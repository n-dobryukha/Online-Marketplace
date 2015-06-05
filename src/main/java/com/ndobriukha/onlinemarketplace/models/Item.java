package com.ndobriukha.onlinemarketplace.models;

import java.sql.Timestamp;
import java.util.Date;

import com.ndobriukha.onlinemarketplace.dao.Identified;

public class Item implements RetrieveFieldsValues, Identified {

	public static enum BooleanType {
		Y, N;
	}
	
	private Integer id = null;
	private Integer sellerId;
	private String title;
	private String description;
	private double startPrice;
	private int timeLeft;
	private Timestamp startBidding;
	private BooleanType buyItNow;
	private double bidIncrement;
	private BooleanType sold;

	public Item() {
		this.sold = BooleanType.N;
	}

	public Item(int sellerId, String title, String description, double startPrice,
			int timeLeft, Timestamp startBidding, BooleanType buyItNow,
			double bidIncrement) {
		this();
		this.sellerId = sellerId;
		this.title = title;
		this.description = description;
		this.startPrice = startPrice;
		this.timeLeft = timeLeft;
		this.startBidding = startBidding;
		this.buyItNow = buyItNow;
		this.bidIncrement = bidIncrement;		
	}

	@Override
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	public Integer getSellerId() {
		return sellerId;
	}

	public void setSellerId(Integer sellerId) {
		this.sellerId = sellerId;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public double getStartPrice() {
		return startPrice;
	}

	public void setStartPrice(double startPrice) {
		this.startPrice = startPrice;
	}

	public int getTimeLeft() {
		return timeLeft;
	}

	public void setTimeLeft(int timeLeft) {
		this.timeLeft = timeLeft;
	}

	public Timestamp getStartBidding() {
		return startBidding;
	}

	public void setStartBidding(Timestamp startBidding) {
		this.startBidding = startBidding;
	}

	public boolean isBuyItNow() {
		return (buyItNow == BooleanType.Y);
	}
	
	public BooleanType getBuyItNow() {
		return buyItNow;
	}

	public void setBuyItNow(BooleanType buyItNow) {
		this.buyItNow = buyItNow;
	}

	public double getBidIncrement() {
		return bidIncrement;
	}

	public void setBidIncrement(double bidIncrement) {
		this.bidIncrement = bidIncrement;
	}

	public BooleanType getSold() {
		return sold;
	}

	public void setSold(BooleanType sold) {
		this.sold = sold;
	}
	
	public void sale() {
		setSold(BooleanType.Y);
	}

	@Override
	public Object[] getFieldsValues() {
		return new Object[] { getSellerId(), getTitle(), getDescription(), getStartPrice(),
				getTimeLeft(), getStartBidding(), getBuyItNow().toString(),
				getBidIncrement(), getSold().toString() };
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(bidIncrement);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result
				+ ((buyItNow == null) ? 0 : buyItNow.hashCode());
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((sellerId == null) ? 0 : sellerId.hashCode());
		result = prime * result + ((sold == null) ? 0 : sold.hashCode());
		result = prime * result
				+ ((startBidding == null) ? 0 : startBidding.hashCode());
		temp = Double.doubleToLongBits(startPrice);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + timeLeft;
		result = prime * result + ((title == null) ? 0 : title.hashCode());
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
		Item other = (Item) obj;
		if (Double.doubleToLongBits(bidIncrement) != Double
				.doubleToLongBits(other.bidIncrement))
			return false;
		if (buyItNow != other.buyItNow)
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (sellerId == null) {
			if (other.sellerId != null)
				return false;
		} else if (!sellerId.equals(other.sellerId))
			return false;
		if (sold != other.sold)
			return false;
		if (startBidding == null) {
			if (other.startBidding != null)
				return false;
		} else if (!startBidding.equals(other.startBidding))
			return false;
		if (Double.doubleToLongBits(startPrice) != Double
				.doubleToLongBits(other.startPrice))
			return false;
		if (timeLeft != other.timeLeft)
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		return true;
	}

}
