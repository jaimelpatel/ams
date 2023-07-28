/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.service.core;

import com.ltlogic.constants.TransactionStatusEnum;
import com.ltlogic.constants.TransactionTypeEnum;
import com.ltlogic.db.entity.Bank;
import com.ltlogic.db.entity.Match;
import com.ltlogic.db.entity.Tournament;
import com.ltlogic.db.entity.Transaction;
import com.ltlogic.db.entity.User;
import com.ltlogic.db.entity.WorldBank;
import com.ltlogic.db.repository.BankRepository;
import com.ltlogic.db.repository.TransactionRepository;
import com.ltlogic.service.common.CommonService;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author jaimel
 */
@Service
@Transactional
public class BankService {

    @Autowired
    private BankRepository bankRepo;

    @Autowired
    private CommonService commonService;

    @Autowired
    private TransactionRepository transactionRepo;

    private static final Logger LOG = LoggerFactory.getLogger(BankService.class);

    public void persistBank(Bank bank) {
        bankRepo.persistBank(bank);
    }

    public void resetBankForAllUsers() {
        bankRepo.resetBankForAllUsers();
    }

    List<TransactionTypeEnum> PROFIT_TYPE_TRANSACTIONS = Arrays.asList(TransactionTypeEnum.DEPOSIT_CASH,
            TransactionTypeEnum.WAGER_BUYIN,
            TransactionTypeEnum.TOURNAMENT_BUYIN);
    List<TransactionTypeEnum> COMMISSION_TYPE_TRANSACTIONS = Arrays.asList(TransactionTypeEnum.WAGER_WIN,
            TransactionTypeEnum.TOURNAMENT_WIN);
    List<TransactionTypeEnum> LOSS_TYPE_TRANSACTIONS = Arrays.asList(TransactionTypeEnum.WITHDRAW_CASH,
            TransactionTypeEnum.WAGER_CANCELLED,
            TransactionTypeEnum.TOURNAMENT_CANCELLED);

    BigDecimal TOURNAMENT_WIN_COMMISSION = new BigDecimal(0.15);
    BigDecimal WAGER_WIN_COMMISSION = new BigDecimal(0.15);

    public void createWorldBankRecordForTransaction(Transaction transaction) throws Exception {
        long transactionPk = transaction.getPk();
        WorldBank worldBank = bankRepo.getWorldBankForTransactionPk(transactionPk);
        if (worldBank != null) {
            throw new Exception("World Bank already exists for transactionPk:" + transactionPk);
        } else {
            WorldBank latestWorldBank = bankRepo.findLatestWorldBank();
            BigDecimal latestCumulativeTransactionAmount = latestWorldBank.getCumulativeTransactionAmount();
            BigDecimal latestTotalAmount = latestWorldBank.getTotalAmount();
            if (latestCumulativeTransactionAmount == null) {
                latestCumulativeTransactionAmount = new BigDecimal(BigInteger.ZERO);
            }
            worldBank = new WorldBank();
            worldBank.setTransaction(transaction);
            BigDecimal transactionAmount = transaction.getTransactionAmount();
            TransactionTypeEnum t = transaction.getTransactionType();
            switch (t) {
                case DEPOSIT_CASH:
                case WAGER_BUYIN:
                case TOURNAMENT_BUYIN: {
                    worldBank.setCumulativeTransactionAmount(latestCumulativeTransactionAmount.add(transactionAmount));
                    worldBank.setTotalAmount(latestTotalAmount.add(transactionAmount));
                    break;
                }
                case WAGER_WIN:
                case TOURNAMENT_WIN: {
                    worldBank.setCumulativeTransactionAmount(latestCumulativeTransactionAmount.add(transaction.getCommissionEarned()));
                    worldBank.setTotalAmount(latestTotalAmount.add(transaction.getCommissionEarned()));
                    break;
                }
                case WITHDRAW_CASH:
                case WAGER_CANCELLED:
                case TOURNAMENT_CANCELLED: {
                    worldBank.setCumulativeTransactionAmount(latestCumulativeTransactionAmount.subtract(transactionAmount));
                    worldBank.setTotalAmount(latestTotalAmount.subtract(transactionAmount));
                    break;
                }
                default: {
                    // default behavior
                }
            }

        }

    }

    public BigDecimal withdrawCash(User user, BigDecimal withdrawalAmount) throws Exception {

        if (user.getUserInfo().getPaypayEmail() == null || user.getUserInfo().getPaypayEmail().isEmpty()) {
            throw new Exception("You must enter your paypal email in your account settings before making a withdrawal.");
        } else if (user.getBank().getTotalAmount().compareTo(withdrawalAmount) == -1) {
            throw new Exception("You cannot withdraw more cash than you have in your account.");
        } else if (withdrawalAmount.compareTo(new BigDecimal("1.0")) <= 0) {
            throw new Exception("You must withdraw an amount greater than $1.00.");
        } else if (withdrawalAmount.compareTo(new BigDecimal("250.0")) > 0) {
            throw new Exception("You must withdraw an amount less than or equal to $250.00.");
        } else if (user.getBank().isHasWithdrawnToday()) {
            throw new Exception("You have already made a withdrawal today and must wait 24 hours before making another.");
        }

        BigDecimal originalAmount = user.getBank().getTotalAmount();
        BigDecimal newAmount = originalAmount.subtract(withdrawalAmount);

        if (newAmount.compareTo(new BigDecimal("1.00")) < 0) {
            withdrawalAmount = withdrawalAmount.subtract(new BigDecimal(1.00));
        } else {
            newAmount = newAmount.subtract(new BigDecimal(1.00));
        }
        user.getBank().setTotalAmount(newAmount);
        user.getBank().setHasWithdrawnToday(true);
        LOG.info("BankService - withdrawCash() : User Pk = " + user.getPk() + ", Withdrawal Amount = " + withdrawalAmount.setScale(2, RoundingMode.HALF_UP) + ", Original Amount = " + originalAmount.setScale(2, RoundingMode.HALF_UP) + ", New Amount = " + newAmount.setScale(2, RoundingMode.HALF_UP));

        //transactions
        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setTransactionType(TransactionTypeEnum.WITHDRAW_CASH);
        transaction.setTransactionAmount(withdrawalAmount);
        transaction.setTransactionStatus(TransactionStatusEnum.PENDING);
        transaction.setTransactionMessage("You have withdrawn NLG Cash from your account. Please wait for a 'COMPLETED' status to see the withdrawn funds in your paypal account.");
        boolean doesIdExist = true;
        while (doesIdExist) {
            int transactionId = commonService.generateRandomId();
            Transaction t = transactionRepo.findTransactionByTransactionId(transactionId);
            if (t == null) {
                transaction.setTransactionId(transactionId);
                doesIdExist = false;
            }
        }
        transactionRepo.persistTransaction(transaction);
        user.getTransactions().add(transaction);
        transaction.setUser(user);

        return withdrawalAmount;
    }

    public void depositCash(User user, BigDecimal depositAmount, String paypalTransactionId) {
        //do paypal stuff here and then confirm the deposit has gone through

        BigDecimal originalAmount = user.getBank().getTotalAmount();
        BigDecimal newAmount = originalAmount.add(depositAmount);
        user.getBank().setTotalAmount(newAmount);

        LOG.info("BankService - depositCash() : User Pk = " + user.getPk() + ", Deposit Amount = " + depositAmount.setScale(2, RoundingMode.HALF_UP) + ", Original Amount = " + originalAmount.setScale(2, RoundingMode.HALF_UP) + ", New Amount = " + newAmount.setScale(2, RoundingMode.HALF_UP) + ", PayPal TransactionId = " + paypalTransactionId);
        //transactions
        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setTransactionType(TransactionTypeEnum.DEPOSIT_CASH);
        transaction.setTransactionAmount(depositAmount);
        transaction.setTransactionStatus(TransactionStatusEnum.COMPLETED);
        transaction.setTransactionMessage("You have deposited NLG Cash into your account.");
        transaction.setPaypalTransactionId(paypalTransactionId);
        boolean doesIdExist = true;
        while (doesIdExist) {
            int transactionId = commonService.generateRandomId();
            Transaction t = transactionRepo.findTransactionByTransactionId(transactionId);
            if (t == null) {
                transaction.setTransactionId(transactionId);
                doesIdExist = false;
            }
        }
        transactionRepo.persistTransaction(transaction);
        user.getTransactions().add(transaction);
        transaction.setUser(user);
    }

    public void transferCash(User userFrom, User userTo, BigDecimal transferAmount) throws Exception {
        //do validation here to make sure the user transferring money has that much or the amount isnt too big
        BigDecimal totalTransferAmountForDay = userFrom.getBank().getTotalTransferAmountForDay().add(transferAmount);

        if (userFrom.getBank().getTotalAmount().compareTo(transferAmount) == -1) {
            throw new Exception("You cannot transfer more cash than you have in your account.");
        } else if (transferAmount.compareTo(new BigDecimal("1.00")) < 0) {
            throw new Exception("You must transfer an amount greater than or equal to $1.00.");
        } else if (transferAmount.compareTo(new BigDecimal("50.00")) > 0 || totalTransferAmountForDay.compareTo(new BigDecimal("50.00")) > 0) {
            throw new Exception("You may only transfer a maximum of $50.00 per day.");
        }

        //user from calc
        BigDecimal userFromOriginalAmount = userFrom.getBank().getTotalAmount();
        BigDecimal userFromNewAmount = userFromOriginalAmount.subtract(transferAmount);
        userFrom.getBank().setTotalAmount(userFromNewAmount);
        BigDecimal transferAmountPerDay = userFrom.getBank().getTotalTransferAmountForDay();
        BigDecimal newTransferAmountPerDay = transferAmountPerDay.add(transferAmount);
        userFrom.getBank().setTotalTransferAmountForDay(newTransferAmountPerDay);

        //user to calculations
        BigDecimal userToOriginalAmount = userTo.getBank().getTotalAmount();
        BigDecimal userToNewAmount = userToOriginalAmount.add(transferAmount);
        userTo.getBank().setTotalAmount(userToNewAmount);

        LOG.info("BankService - transferCash() : UserFrom Pk = " + userFrom.getPk() + ", UserTo Pk = " + userTo.getPk() + ", Transfer Amount = " + transferAmount.setScale(2, RoundingMode.HALF_UP) + ", userFromOriginalAmount = " + userFromOriginalAmount.setScale(2, RoundingMode.HALF_UP) + ", userFromNewAmount = "
                + userFromNewAmount.setScale(2, RoundingMode.HALF_UP) + ", userToOriginalAmount = " + userToOriginalAmount.setScale(2, RoundingMode.HALF_UP) + ", userToNewAmount = " + userToNewAmount.setScale(2, RoundingMode.HALF_UP));

        //transactions
        Transaction transaction = new Transaction();
        transaction.setUser(userFrom);
        transaction.setUserPkTransferTo(userTo.getPk());
        transaction.setTransactionType(TransactionTypeEnum.TRANSFER_CASH);
        transaction.setTransactionAmount(transferAmount);
        transaction.setTransactionStatus(TransactionStatusEnum.COMPLETED);
        transaction.setTransactionMessage("NLG Cash has been transferred from user '" + userFrom.getUsername() + "' to user '" + userTo.getUsername() + "'.");
        transaction.setTransactionTransferToMessage("You have received NLG Cash from user '" + userFrom.getUsername() + "'");
        boolean doesIdExist = true;
        while (doesIdExist) {
            int transactionId = commonService.generateRandomId();
            Transaction t = transactionRepo.findTransactionByTransactionId(transactionId);
            if (t == null) {
                transaction.setTransactionId(transactionId);
                doesIdExist = false;
            }
        }
        transactionRepo.persistTransaction(transaction);
        userFrom.getTransactions().add(transaction);
        transaction.setUser(userFrom);

    }

    //call this method on eahc users match accept for cash matches
    public void wagerMatchBuyIn(User user, Match match) {
        //do paypal stuff here and then confirm the deposit has gone through

        BigDecimal originalAmount = user.getBank().getTotalAmount();
        BigDecimal newAmount = originalAmount.subtract(match.getMatchInfo().getWagerAmountPerMember()).setScale(2, RoundingMode.HALF_UP);
        user.getBank().setTotalAmount(newAmount);

        LOG.info("BankService - wagerMatchBuyIn() : User Pk = " + user.getPk() + ", Match Pk = " + match.getPk() + ", Original Amount = " + originalAmount.setScale(2, RoundingMode.HALF_UP) + ", Wager Match Buy In = " + match.getMatchInfo().getWagerAmountPerMember().setScale(2, RoundingMode.HALF_UP) + ", New Amount = " + newAmount.setScale(2, RoundingMode.HALF_UP));

        //transactions
        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setTransactionType(TransactionTypeEnum.WAGER_BUYIN);
        transaction.setTransactionAmount(match.getMatchInfo().getWagerAmountPerMember());
        transaction.setTransactionStatus(TransactionStatusEnum.COMPLETED);
        transaction.setTransactionMessage("You have bought into a wager match (ID #" + match.getMatchId() + ").");
        boolean doesIdExist = true;
        while (doesIdExist) {
            int transactionId = commonService.generateRandomId();
            Transaction t = transactionRepo.findTransactionByTransactionId(transactionId);
            if (t == null) {
                transaction.setTransactionId(transactionId);
                doesIdExist = false;
            }
        }

        transactionRepo.persistTransaction(transaction);
        user.getTransactions().add(transaction);
        transaction.setUser(user);

        match.getTransactions().add(transaction);
        transaction.setMatch(match);
    }

    //happens when someone declines or the 2 teams agree to a cancel match
    public void wagerMatchCancelled(User user, Match match) {
        //do paypal stuff here and then confirm the deposit has gone through

        BigDecimal originalAmount = user.getBank().getTotalAmount();
        BigDecimal newAmount = originalAmount.add(match.getMatchInfo().getWagerAmountPerMember()).setScale(2, RoundingMode.HALF_UP);
        user.getBank().setTotalAmount(newAmount);

        LOG.info("BankService - wagerMatchCancelled() : User Pk = " + user.getPk() + ", Match Pk = " + match.getPk() + ", Original Amount = " + originalAmount.setScale(2, RoundingMode.HALF_UP) + ", Wager Match Buy In = " + match.getMatchInfo().getWagerAmountPerMember().setScale(2, RoundingMode.HALF_UP) + ", New Amount = " + newAmount.setScale(2, RoundingMode.HALF_UP));

        //transactions
        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setTransactionType(TransactionTypeEnum.WAGER_CANCELLED);
        transaction.setTransactionAmount(match.getMatchInfo().getWagerAmountPerMember());
        transaction.setTransactionStatus(TransactionStatusEnum.COMPLETED);
        transaction.setTransactionMessage("A wager match your were in has been cancelled (ID #" + match.getMatchId() + ").");
        boolean doesIdExist = true;
        while (doesIdExist) {
            int transactionId = commonService.generateRandomId();
            Transaction t = transactionRepo.findTransactionByTransactionId(transactionId);
            if (t == null) {
                transaction.setTransactionId(transactionId);
                doesIdExist = false;
            }
        }

        transactionRepo.persistTransaction(transaction);
        user.getTransactions().add(transaction);
        transaction.setUser(user);

        match.getTransactions().add(transaction);
        transaction.setMatch(match);
    }

    public void wagerMatchWin(User user, Match match, BigDecimal amountWon) {
        //need to do calc here based on what amount we are taking from the pot
        BigDecimal originalAmount = user.getBank().getTotalAmount();
        BigDecimal newAmount = originalAmount.add(amountWon).setScale(2, RoundingMode.HALF_UP);
        user.getBank().setTotalAmount(newAmount);

        BigDecimal userTotalEarnings = user.getRankEarnings().getTotalEarnings();
        BigDecimal newTotalEarnings = userTotalEarnings.add(amountWon).setScale(2, RoundingMode.HALF_UP);
        user.getRankEarnings().setTotalEarnings(newTotalEarnings);

        LOG.info("BankService - wagerMatchWin() : User Pk = " + user.getPk() + ", Match Pk = " + match.getPk() + ", Original Amount = " + originalAmount.setScale(2, RoundingMode.HALF_UP) + ", Amount Won = " + amountWon.setScale(2, RoundingMode.HALF_UP) + ", New Amount = " + newAmount.setScale(2, RoundingMode.HALF_UP));

        //transactions
        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setTransactionType(TransactionTypeEnum.WAGER_WIN);
        transaction.setTransactionAmount(amountWon.setScale(2, RoundingMode.HALF_UP));
        transaction.setTransactionStatus(TransactionStatusEnum.COMPLETED);
        transaction.setTransactionMessage("You have won a wager match (ID #" + match.getMatchId() + ").");
        boolean doesIdExist = true;
        while (doesIdExist) {
            int transactionId = commonService.generateRandomId();
            Transaction t = transactionRepo.findTransactionByTransactionId(transactionId);
            if (t == null) {
                transaction.setTransactionId(transactionId);
                doesIdExist = false;
            }
        }

        transactionRepo.persistTransaction(transaction);
        user.getTransactions().add(transaction);
        transaction.setUser(user);

        match.getTransactions().add(transaction);
        transaction.setMatch(match);
        System.out.println("BEGINNGING OF WAGER MATCH WIN INSIDE");
    }

    //call this method on each users tournament accept for cash tournaments
    public void tournamentBuyIn(User user, Tournament tournament) {
        //do paypal stuff here and then confirm the deposit has gone through

        BigDecimal originalAmount = user.getBank().getTotalAmount();
        BigDecimal newAmount = originalAmount.subtract(tournament.getTournamentInfo().getWagerAmountPerMember()).setScale(2, RoundingMode.HALF_UP);
        user.getBank().setTotalAmount(newAmount);

        LOG.info("BankService - tournamentBuyIn() : User Pk = " + user.getPk() + ", Tournament Pk = " + tournament.getPk() + ", Original Amount = " + originalAmount.setScale(2, RoundingMode.HALF_UP) + ", Tournament Buy In = " + tournament.getTournamentInfo().getWagerAmountPerMember().setScale(2, RoundingMode.HALF_UP) + ", New Amount = " + newAmount.setScale(2, RoundingMode.HALF_UP));

        //transactions
        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setTransactionType(TransactionTypeEnum.TOURNAMENT_BUYIN);
        transaction.setTransactionAmount(tournament.getTournamentInfo().getWagerAmountPerMember());
        transaction.setTransactionStatus(TransactionStatusEnum.COMPLETED);
        transaction.setTransactionMessage("You have bought into a tournament (ID #" + tournament.getTournamentId() + ").");
        boolean doesIdExist = true;
        while (doesIdExist) {
            int transactionId = commonService.generateRandomId();
            Transaction t = transactionRepo.findTransactionByTransactionId(transactionId);
            if (t == null) {
                transaction.setTransactionId(transactionId);
                doesIdExist = false;
            }
        }

        transactionRepo.persistTransaction(transaction);
        user.getTransactions().add(transaction);
        transaction.setUser(user);

        tournament.getTransactions().add(transaction);
        transaction.setTournament(tournament);
    }

    //happens when someone declines to join the tournament on the team, the team is killed and the members who did accept are now refunded
    public void tournamentCancelled(User user, Tournament tournament) {
        //do paypal stuff here and then confirm the deposit has gone through

        BigDecimal originalAmount = user.getBank().getTotalAmount();
        BigDecimal newAmount = originalAmount.add(tournament.getTournamentInfo().getWagerAmountPerMember()).setScale(2, RoundingMode.HALF_UP);
        user.getBank().setTotalAmount(newAmount);

        LOG.info("BankService - tournamentCancelled() : User Pk = " + user.getPk() + ", Tournament Pk = " + tournament.getPk() + ", Original Amount = " + originalAmount.setScale(2, RoundingMode.HALF_UP) + ", Tournament Buy In = " + tournament.getTournamentInfo().getWagerAmountPerMember().setScale(2, RoundingMode.HALF_UP) + ", New Amount = " + newAmount.setScale(2, RoundingMode.HALF_UP));

        //transactions
        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setTransactionType(TransactionTypeEnum.TOURNAMENT_CANCELLED);
        transaction.setTransactionAmount(tournament.getTournamentInfo().getWagerAmountPerMember());
        transaction.setTransactionStatus(TransactionStatusEnum.COMPLETED);
        transaction.setTransactionMessage("You have left a tournament (ID #" + tournament.getTournamentId() + ").");
        boolean doesIdExist = true;
        while (doesIdExist) {
            int transactionId = commonService.generateRandomId();
            Transaction t = transactionRepo.findTransactionByTransactionId(transactionId);
            if (t == null) {
                transaction.setTransactionId(transactionId);
                doesIdExist = false;
            }
        }

        transactionRepo.persistTransaction(transaction);
        user.getTransactions().add(transaction);
        transaction.setUser(user);

        tournament.getTransactions().add(transaction);
        transaction.setTournament(tournament);
    }

    public void tournamentWin(User user, Tournament tournament, BigDecimal amountWon) {
        //need to do calc here based on what amount we are taking from the pot
        BigDecimal originalAmount = user.getBank().getTotalAmount();
        BigDecimal newAmount = originalAmount.add(amountWon).setScale(2, RoundingMode.HALF_UP);
        user.getBank().setTotalAmount(newAmount);

        BigDecimal userTotalEarnings = user.getRankEarnings().getTotalEarnings();
        BigDecimal newTotalEarnings = userTotalEarnings.add(amountWon).setScale(2, RoundingMode.HALF_UP);
        user.getRankEarnings().setTotalEarnings(newTotalEarnings);

        LOG.info("BankService - tournamentWin() : User Pk = " + user.getPk() + ", Tournament Pk = " + tournament.getPk() + ", Original Amount = " + originalAmount.setScale(2, RoundingMode.HALF_UP) + ", Amount Won = " + amountWon.setScale(2, RoundingMode.HALF_UP) + ", New Amount = " + newAmount.setScale(2, RoundingMode.HALF_UP));

        //transactions
        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setTransactionType(TransactionTypeEnum.TOURNAMENT_WIN);
        transaction.setTransactionAmount(amountWon.setScale(2, RoundingMode.HALF_UP));
        transaction.setTransactionStatus(TransactionStatusEnum.COMPLETED);
        transaction.setTransactionMessage("You have won a tournament (ID #" + tournament.getTournamentId() + ").");
        boolean doesIdExist = true;
        while (doesIdExist) {
            int transactionId = commonService.generateRandomId();
            Transaction t = transactionRepo.findTransactionByTransactionId(transactionId);
            if (t == null) {
                transaction.setTransactionId(transactionId);
                doesIdExist = false;
            }
        }
        transactionRepo.persistTransaction(transaction);
        user.getTransactions().add(transaction);
        transaction.setUser(user);

        tournament.getTransactions().add(transaction);
        transaction.setTournament(tournament);
    }

    //when a user donates to a cash wager
    public Transaction wagerDonate(User user, Match match, BigDecimal amountDonated) {

        BigDecimal originalAmount = user.getBank().getTotalAmount();
        BigDecimal newAmount = originalAmount.subtract(amountDonated);
        user.getBank().setTotalAmount(newAmount);

        BigDecimal currentMatchPotAmount = match.getMatchInfo().getPotAmount();
        BigDecimal newMatchPotAmount = currentMatchPotAmount.add(amountDonated);
        match.getMatchInfo().setPotAmount(newMatchPotAmount);

        //transactions
        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setTransactionType(TransactionTypeEnum.WAGER_DONATE);
        transaction.setTransactionAmount(amountDonated);

        transaction.setTransactionStatus(TransactionStatusEnum.PENDING);
        boolean doesIdExist = true;
        while (doesIdExist) {
            int transactionId = commonService.generateRandomId();
            Transaction t = transactionRepo.findTransactionByTransactionId(transactionId);
            if (t == null) {
                transaction.setTransactionId(transactionId);
                doesIdExist = false;
            }
        }

        transactionRepo.persistTransaction(transaction);
        user.getTransactions().add(transaction);
        transaction.setUser(user);

        match.getTransactions().add(transaction);
        transaction.setMatch(match);
        match.getMatchDonationTransactions().add(transaction);
        return transaction;
    }

    //when a user donates to a cash tournament
    public Transaction tournamentDonate(User user, Tournament tournament, BigDecimal amountDonated) {

        BigDecimal originalAmount = user.getBank().getTotalAmount();
        BigDecimal newAmount = originalAmount.subtract(amountDonated);
        user.getBank().setTotalAmount(newAmount);

        BigDecimal currentTournamentPotAmount = tournament.getTournamentInfo().getPotAmount();
        BigDecimal newTournamentPotAmount = currentTournamentPotAmount.add(amountDonated);
        tournament.getTournamentInfo().setPotAmount(newTournamentPotAmount);

        //transactions
        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setTransactionType(TransactionTypeEnum.TOURNAMENT_DONATE);
        transaction.setTransactionAmount(amountDonated);

        transaction.setTransactionStatus(TransactionStatusEnum.COMPLETED);
        boolean doesIdExist = true;
        while (doesIdExist) {
            int transactionId = commonService.generateRandomId();
            Transaction t = transactionRepo.findTransactionByTransactionId(transactionId);
            if (t == null) {
                transaction.setTransactionId(transactionId);
                doesIdExist = false;
            }
        }
        transactionRepo.persistTransaction(transaction);
        user.getTransactions().add(transaction);
        transaction.setUser(user);

        tournament.getTransactions().add(transaction);
        transaction.setTournament(tournament);
        tournament.getTournamentDonationTransactions().add(transaction);
        return transaction;
    }

    public List<Transaction> getAllTransactionsForUser(long userPk, int pageNumber) {
        return transactionRepo.getTransactionsForUser(userPk, pageNumber);
    }

    
    public Integer getTotalTransactionsForUser(long userPk) {
        return transactionRepo.getTotalTransactionsForUser(userPk);
    }
}
