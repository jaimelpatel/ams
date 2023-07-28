/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.constants;

/**
 *
 * @author jaimel
 */
public enum TimeZoneEnum {
    UTC_MINUS_11(1, "(UTC-11:00) Pacific/Midway"),
    UTC_MINUS_10(2, "(UTC-10:00) US/Hawaii"),
    UTC_MINUS_9_30(3, "(UTC-09:30) Pacific/Marquesas"),
    UTC_MINUS_8(5, "(UTC-09:00) US/Alaska"),
    UTC_MINUS_7_LA(6, "(UTC-08:00) America/Los_Angeles"),
//    UTC_MINUS_7_AZ(7, "(UTC-07:00) America/Phoenix"),
    UTC_MINUS_7_VAN(8, "(UTC-08:00) America/Vancouver"),
    UTC_MINUS_7_TIJ(9, "(UTC-08:00) America/Tijuana"),
    UTC_MINUS_6_DEN(10, "(UTC-07:00) America/Denver"),
    UTC_MINUS_6_EL(11, "(UTC-07:00) America/Phoenix"),
    UTC_MINUS_6_ED(12, "(UTC-07:00) America/Edmonton"),
    UTC_MINUS_5_CHI(13, "(UTC-06:00) America/Chicago"),
    UTC_MINUS_5_MC(14, "(UTC-06:00) America/Mexico_City"),
    UTC_MINUS_5_WIN(15, "(UTC-06:00) America/Winnipeg"),
    UTC_MINUS_4_NY(16, "(UTC-05:00) America/New_York"),
    UTC_MINUS_4_TOR(17, "(UTC-05:00) America/Toronto"),
    UTC_MINUS_4_PR(18, "(UTC-04:00) America/Puerto_Rico"),
//    UTC_MINUS_3_BR(19, "(UTC-03:00) Brazil/East"),
    UTC_MINUS_3_CA(20, "(UTC-04:00) Canada/Atlantic"),
    UTC_MINUS_2_30_ST(21, "(UTC-03:30) America/St_Johns"),
    UTC_MINUS_2_30_NF(22, "(UTC-03:30) Canada/Newfoundland"),
    UTC_MINUS_2_MIQ(23, "(UTC-03:00) America/Miquelon"),
    UTC_MINUS_2_SP(59, "(UTC-02:00) America/Sao_Paulo"),
    UTC_MINUS_1_CV(24, "(UTC-01:00) Atlantic/Cape_Verde"),
    UTC_0_SH(25, "(UTC+00:00) Atlantic/St_Helena"),
    UTC_PLUS_1_LON(26, "(UTC+00:00) Europe/London"),
    UTC_PLUS_1_DUB(27, "(UTC+00:00) Europe/Dublin"),
    UTC_PLUS_1_LIS(28, "(UTC+00:00) Europe/Lisbon"),
    UTC_PLUS_2_STO(29, "(UTC+01:00) Europe/Stockholm"),
    UTC_PLUS_2_WAR(30, "(UTC+01:00) Europe/Warsaw"),
    UTC_PLUS_2_PA(31, "(UTC+01:00) Europe/Paris"),
    UTC_PLUS_2_VAT(32, "(UTC+01:00) Europe/Vatican"),
    UTC_PLUS_2_LUX(33, "(UTC+01:00) Europe/Luxembourg"),
    UTC_PLUS_2_ROM(34, "(UTC+01:00) Europe/Rome"),
    UTC_PLUS_2_MAD(35, "(UTC+01:00) Europe/Madrid"),
    UTC_PLUS_2_COP(36, "(UTC+01:00) Europe/Copenhagen"),
    UTC_PLUS_2_BER(37, "(UTC+01:00) Europe/Berlin"),
    UTC_PLUS_2_VIE(38, "(UTC+01:00) Europe/Vienna"),
    UTC_PLUS_2_BRU(39, "(UTC+01:00) Europe/Brussels"),
    UTC_PLUS_2_AM(40, "(UTC+01:00) Europe/Amsterdam"),
    UTC_PLUS_2_JE(60, "(UTC+02:00) Asia/Jerusalem"),
    UTC_PLUS_2_AFR(61, "(UTC+02:00) Africa/Cairo"),
    UTC_PLUS_3_MOS(41, "(UTC+03:00) Europe/Moscow"),
    UTC_PLUS_3_MIN(42, "(UTC+03:00) Europe/Minsk"),
    UTC_PLUS_3_IST(43, "(UTC+03:00) Europe/Istanbul"),
    UTC_PLUS_3_KIEV(44, "(UTC+03:00) Europe/Kiev"),
    UTC_PLUS_3_ATH(45, "(UTC+03:00) Europe/Athens"),
    UTC_PLUS_4_30_TEH(48, "(UTC+03:30) Asia/Tehran"),
    UTC_PLUS_4_SAM(46, "(UTC+04:00) Europe/Samara"),
    UTC_PLUS_4_DUB(47, "(UTC+04:00) Asia/Dubai"),
    UTC_PLUS_5_MAL(49, "(UTC+05:00) Indian/Maldives"),
    UTC_PLUS_6_CHA(50, "(UTC+06:00) Indian/Chagos"),
    UTC_PLUS_7_BAN(51, "(UTC+07:00) Asia/Bangkok"),
    UTC_PLUS_8_HON(52, "(UTC+08:00) Asia/Hong_Kong"),
    UTC_PLUS_9_TOK(53, "(UTC+09:00) Asia/Tokyo"),
    UTC_PLUS_10_AUS(54, "(UTC+10:00) Australia/Sydney"),
    UTC_PLUS_11_NOR(55, "(UTC+11:00) Pacific/Norfolk"),
    UTC_PLUS_12_FIJ(56, "(UTC+12:00) Pacific/Fiji"),
    UTC_PLUS_13_API(57, "(UTC+13:00) Pacific/Apia"),
    UTC_PLUS_14_KIR(58, "(UTC+14:00) Pacific/Kiritimati");

    private final int id;
    private final String desc;

    private TimeZoneEnum(int id, String desc) {
        this.id = id;
        this.desc = desc;
    }

    public int getTimeZonesEnumId() {
        return id;
    }

    public String getTimeZonesEnumDesc() {
        return desc;
    }

    public static TimeZoneEnum getTimeZonesEnumById(int id) {
        for (TimeZoneEnum rel : TimeZoneEnum.values()) {
            if (rel.getTimeZonesEnumId()== id) {
                return rel;
            }
        }
        return null;
    }
}
