package com.example.yiska.project_part2.model.entities;

public class Trip {

    public enum STATUS {
        AVAILABLE, ON, DONE
    }

    private static final long serialVersionUID = 1L;

    protected String id;
    protected STATUS status;
    protected String placeBegin;
    protected String destination;
    protected String hourBegin;
    protected String hourEnd;
    protected String costumerName;
    protected String costumerTel;
    protected String costumerEmail;
    protected String driverName;
    private String creditCard;
    private String tripDate = "";
    private float distance;

    public Trip() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public STATUS getStatus() {
        return status;
    }

    public void setStatus(STATUS status) {
        this.status = status;
    }

    public String getPlaceBegin() {
        return placeBegin;
    }

    public void setPlaceBegin(String placeBegin) {
        this.placeBegin = placeBegin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getHourBegin() {
        return hourBegin;
    }

    public void setHourBegin(String hourBegin) {
        this.hourBegin = hourBegin;
    }

    public String getHourEnd() {
        return hourEnd;
    }

    public void setHourEnd(String hourEnd) {
        this.hourEnd = hourEnd;
    }

    public String getCostumerName() {
        return costumerName;
    }

    public void setCostumerName(String costumerName) {
        this.costumerName = costumerName;
    }

    public String getCostumerTel() {
        return costumerTel;
    }

    public void setCostumerTel(String costumerTel) {
        this.costumerTel = costumerTel;
    }

    public String getCostumerEmail() {
        return costumerEmail;
    }

    public void setCostumerEmail(String costumerEmail) {
        this.costumerEmail = costumerEmail;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getCreditCard() {
        return creditCard;
    }

    public void setCreditCard(String creditCard) {
        this.creditCard = creditCard;
    }

    public String getTripDate() {
        return tripDate;
    }

    public void setTripDate(String rideDate) {
        this.tripDate = rideDate;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Trip trip = (Trip) o;
        return placeBegin.equals(trip.getPlaceBegin()) &&
                destination.equals(trip.getDestination()) &&
                costumerName.equals(trip.getCostumerName()) &&
                costumerTel.equals(trip.getCostumerTel()) &&
                costumerEmail.equals(trip.getCostumerEmail()) &&
                driverName == trip.getDriverName();
    }

    @Override
    public String toString() {
        return "Trip{" +
                "placeBegin='" + placeBegin + '\'' +
                ", destination='" + destination + '\'' +
                ", costumerName='" + costumerName + '\'' +
                ", costumerTel='" + costumerTel + '\'' +
                ", costumerEmail='" + costumerEmail + '\'' +
                ", idDriver=" + driverName +
                '}';
    }
}
