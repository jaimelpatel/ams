/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.service.core;

import com.google.common.base.Strings;
import com.ltlogic.db.entity.PasswordResetToken;
import com.ltlogic.db.entity.User;
import com.ltlogic.db.entity.VerificationToken;
import com.ltlogic.db.repository.PasswordResetTokenRepository;
import com.ltlogic.db.repository.UserRepository;
import com.ltlogic.db.repository.VerificationTokenRepository;
import com.ltlogic.applicationevent.event.OnRegistrationCompleteEvent;
import com.ltlogic.pojo.SupportTicketPojo;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author Jaimel
 */
@Service
@Transactional
public class EmailService {

//    public static void main(String[] args){
//        sendEmailTest();
//    }
    @Autowired
    UserRepository userRepo;

    @Autowired
    VerificationTokenRepository verTokenRepo;

    @Autowired
    PasswordResetTokenRepository pasTokenRepo;

    @Autowired
    UserService userService;

    private static final Logger LOG = LoggerFactory.getLogger(EmailService.class);

    String fromEmail = "support@nextlevelgamingonline.com";//support@nextlevelgamingonline.com
    String password = "SodaCan1337";

//    public static void main(String[] args){
//        EmailService e = new EmailService();
//        e.sendEmailTest();
//    }
    public Properties setupEmailProperties() {

        Properties properties = new Properties();
        //props.put("mail.smtp.port", "465");
//props.put("mail.smtp.ssl.enable", "true");

//        properties.setProperty("mail.smtp.host", "smtp.zoho.com");
//        properties.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
//        properties.setProperty("mail.smtp.socketFactory.fallback", "false");
//        properties.setProperty("mail.smtp.port", "465");
//        properties.setProperty("mail.smtp.socketFactory.port", "465");
//        properties.put("mail.smtp.starttls.enable", "true");
//        properties.put("mail.smtp.auth", "true");
//        properties.put("mail.debug", "true");
//        properties.put("mail.store.protocol", "pop3");
//        properties.put("mail.transport.protocol", "smtp");
//        properties.put("mail.debug.auth", "true");
//        properties.setProperty( "mail.pop3.socketFactory.fallback", "false");
        properties.put("mail.debug", "true");

        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.port", "587");//587
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");//smtp.gmail.com -- smtp.sendgrid.net -- smtp.zoho.com
        return properties;
    }

    public Session createNewSession(Properties properties, String email, String password) {
        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(email, password);
            }
        });
        return session;
    }

    private String getAppUrl(HttpServletRequest request) {
        String appURL = "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
        LOG.info("ApplicationURL: " + appURL + " XXXXXXXXXXXXXX");
        return "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }

    public void sendEmailTest() {
        long startTime = System.currentTimeMillis();

        Properties props = setupEmailProperties();

        //for ssl
//        props.put("mail.smtp.socketFactory.port", "465");
//        props.put("mail.smtp.socketFactory.class",
//                 "javax.net.ssl.SSLSocketFactory");
        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        });

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("johnsmithbusiness2017@gmail.com"));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse("jaimelpatel@yahoo.com"));
            message.setSubject("Testing Method In Html");
            message.setText("Dear Mail Crawler,"
                    + "\n\n No spam to my email, please!");

            Transport.send(message);

            System.out.println("Done");
            long endTime = System.currentTimeMillis();

            System.out.println("That took " + (endTime - startTime) / 1000 + " seconds");

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendVerificationEmail(OnRegistrationCompleteEvent event, User user, String token) throws UnsupportedEncodingException {
        Properties props = setupEmailProperties();

        //for ssl
//        props.put("mail.smtp.socketFactory.port", "465");
//        props.put("mail.smtp.socketFactory.class",
//                 "javax.net.ssl.SSLSocketFactory");
        Session session = createNewSession(props, fromEmail, password);

        final String confirmationUrl = event.getAppUrl() + "/registrationConfirm.html?token=" + token;

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(user.getEmail()));
            message.setSubject("Registration Confirmation");
            message.setText("Hello " + user.getUsername()
                    + ",\n\n" + "Thanks for signing up!  Please go to " + confirmationUrl + " to verify your account." + "\n\n" + "-NLG");
            message.setFrom(new InternetAddress("support@nextlevelgamingonline.com", "Next Level Gaming"));

            String messageContents = "Dear " + user.getUsername()
                    + ",\n\n" + "Your account has been created.  Go to " + confirmationUrl + " to activate your account.";

            Transport.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public void resendVerificationEmail(String email, HttpServletRequest request) {
        Properties props = setupEmailProperties();
        User user = userRepo.findByEmail(email);
        //for ssl
//        props.put("mail.smtp.socketFactory.port", "465");
//        props.put("mail.smtp.socketFactory.class",
//                 "javax.net.ssl.SSLSocketFactory");
        Session session = createNewSession(props, fromEmail, password);

        VerificationToken oldToken = verTokenRepo.findByUser(user);
        VerificationToken newToken = userService.generateNewVerificationToken(oldToken.getToken());

        String confirmationUrl = getAppUrl(request) + "/registrationConfirm.html?token=" + newToken.getToken();

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(user.getEmail()));
            message.setSubject("Registration Confirmation");
            message.setText("Dear " + user.getUsername()
                    + ",\n\n" + "Your account has been created.  Go to " + confirmationUrl + " to activate your account.");

            Transport.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendPasswordResetEmail(String email, HttpServletRequest request) throws UnsupportedEncodingException {
        User user = userRepo.findByEmail(email);
//        if (user.isIsVerified()) {
        if (user != null) {
            Properties props = setupEmailProperties();

            Session session = createNewSession(props, fromEmail, password);
            PasswordResetToken token = pasTokenRepo.findByUser(user);
            if (token == null) {
                token = userService.createPasswordResetTokenForUser(user);
            } else {
                token = userService.generateNewPasswordResetToken(token.getToken());
            }

            String passwordResetnUrl = getAppUrl(request) + "/password/reset?token=" + token.getToken();

            try {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(fromEmail));
                message.setRecipients(Message.RecipientType.TO,
                        InternetAddress.parse(user.getEmail()));
                message.setSubject("Password Reset");
                message.setText("Hello " + user.getUsername()
                        + ",\n\n" + "You have requested to change your password.  Please go to " + passwordResetnUrl + " to reset your password." + "\n\n" + "-NLG");
                message.setFrom(new InternetAddress("support@nextlevelgamingonline.com", "Next Level Gaming"));
                Transport.send(message);

            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
        }
//        }else{
//            throw new RuntimeException("User has not verified their email address. Cannot reset password");
//        }
    }

    public void sendForgottenUsernameEmail(String email, HttpServletRequest request) throws UnsupportedEncodingException {
        User user = userRepo.findByEmail(email);
        if (user != null) {
            Properties props = setupEmailProperties();

            Session session = createNewSession(props, fromEmail, password);

            try {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(fromEmail));
                message.setRecipients(Message.RecipientType.TO,
                        InternetAddress.parse(user.getEmail()));
                message.setSubject("Forgotten Username");
                message.setText("Hello! " + "\n\n" + "The username of your account is: " + user.getUsername() + "\n\n" + "-NLG");
                message.setFrom(new InternetAddress("support@nextlevelgamingonline.com", "Next Level Gaming"));
                Transport.send(message);

            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
        } else {

        }
    }

    public void sendSupportTicketEmail(SupportTicketPojo supportTicketPojo) throws UnsupportedEncodingException {
        
            Properties props = setupEmailProperties();

            Session session = createNewSession(props, fromEmail, password);

            try {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(fromEmail));
                message.setRecipients(Message.RecipientType.TO,
                        InternetAddress.parse(supportTicketPojo.getEmail()));
                message.setSubject(supportTicketPojo.getSubject());
                message.setText(supportTicketPojo.getMessage());
                message.setFrom(new InternetAddress("support@nextlevelgamingonline.com", "NLG Support"));
                Transport.send(message);

            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
    }

}
