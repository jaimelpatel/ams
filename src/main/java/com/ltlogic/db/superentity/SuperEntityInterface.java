package com.ltlogic.db.superentity;

import java.time.LocalDateTime;

public interface SuperEntityInterface {
	
	 public long getPk();
	    public void setPk(long pk);
	    @Override
	    public String toString();
	    public LocalDateTime getRowUpdatedTimestamp();
	    public void setRowUpdatedTimestamp(LocalDateTime rowUpdatedTimestamp);


}
