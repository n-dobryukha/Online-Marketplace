package com.ndobriukha.onlinemarketplace.models;

import java.sql.Timestamp;

import com.ndobriukha.onlinemarketplace.dao.Identified;

public class Bid implements RetrieveFieldsValues, Identified {

	private Integer id = null;
	private Integer itemId;
	private Integer bidderId;	
	private double amount;
	private Timestamp timestamp;
	
	public Bid() {
	}	

	public Bid(Integer bidderId, Integer itemId, double amount, Timestamp timestamp) {
		this.bidderId = bidderId;
		this.itemId = itemId;
		this.amount = amount;
		this.timestamp = timestamp;
	}

	@Override	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getItemId() {
		return itemId;
	}

	public void setItemId(Integer itemId) {
		this.itemId = itemId;
	}
	
	public Integer getBidderId() {
		return bidderId;
	}

	public void setBidderId(Integer bidderId) {
		this.bidderId = bidderId;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public Object[] getFieldsValues() {
		return new Object[] { getItemId(), getBidderId(), getAmount(), getTimestamp() };
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(amount);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result
				+ ((bidderId == null) ? 0 : bidderId.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((itemId == null) ? 0 : itemId.hashCode());
		result = prime * result
				+ ((timestamp == null) ? 0 : timestamp.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Bid other = (Bid) obj;
		if (Double.doubleToLongBits(amount) != Double
				.doubleToLongBits(other.amount))
			return false;
		if (bidderId == null) {
			if (other.bidderId != null)
				return false;
		} else if (!bidderId.equals(other.bidderId))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (itemId == null) {
			if (other.itemId != null)
				return false;
		} else if (!itemId.equals(other.itemId))
			return false;
		if (timestamp == null) {
			if (other.timestamp != null)
				return false;
		} else if (!timestamp.equals(other.timestamp))
			return false;
		return true;
	}	
	
}
