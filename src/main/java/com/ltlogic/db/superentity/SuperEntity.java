package com.ltlogic.db.superentity;

import com.ltlogic.DateTimeUtil;
import com.ltlogic.constants.TimeZoneEnum;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

@MappedSuperclass
public class SuperEntity implements SuperEntityInterface {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk", unique = true, nullable = false)
    protected long pk;

    protected LocalDateTime rowCreatedTimestamp;

    protected LocalDateTime rowUpdatedTimestamp;

    @Override
    public long getPk() {
        return pk;
    }

    @Override
    public void setPk(long pk) {
        this.pk = pk;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " [ pk = " + pk + " ]";
    }

    public String originalToString() {
        return super.toString();
    }

    @Override
    public LocalDateTime getRowUpdatedTimestamp() {
        return rowUpdatedTimestamp;
    }

    @Override
    public void setRowUpdatedTimestamp(LocalDateTime rowUpdatedTimestamp) {
        this.rowUpdatedTimestamp = rowUpdatedTimestamp;
    }

    @PreUpdate
    private void onUpdate() {
        this.rowUpdatedTimestamp = DateTimeUtil.getDefaultLocalDateTimeNow();
    }

    @PrePersist
    private void onCreate() {
        this.rowCreatedTimestamp = DateTimeUtil.getDefaultLocalDateTimeNow();
    }

    public LocalDateTime getRowCreatedTimestamp() {
        return rowCreatedTimestamp;
    }

    //pass this user.getUserInfo().getTimeZone() in param
    public String getRowCreatedTimestampTimezone(TimeZoneEnum tze) {
        LocalDateTime createdUserTime = DateTimeUtil.getUserLocalDateTime(rowCreatedTimestamp, tze);
        return DateTimeUtil.formatLocalDateTime(createdUserTime);
    }

    public void setRowCreatedTimestamp(LocalDateTime rowCreatedTimestamp) {
        this.rowCreatedTimestamp = rowCreatedTimestamp;
    }

//    public static LocalDateTime getLocalDateTimeInUTC1() {
//        ZonedDateTime nowUTC = ZonedDateTime.now(ZoneOffset.UTC);
//        return nowUTC.toLocalDateTime();
//    }
}
