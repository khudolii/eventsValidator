package logic.entities;

public class Truck  implements Comparable<Truck>{
    private long truckId;
    private String truckNumber;
    private String vinNumber;

    public Truck(long truckId, String truckNumber, String vinNumber) {
        this.truckId = truckId;
        this.truckNumber = truckNumber;
        this.vinNumber = vinNumber;
    }

    public long getTruckId() {
        return truckId;
    }

    public void setTruckId(long truckId) {
        this.truckId = truckId;
    }

    public String getTruckNumber() {
        return truckNumber;
    }

    public void setTruckNumber(String truckNumber) {
        this.truckNumber = truckNumber;
    }

    public String getVinNumber() {
        return vinNumber;
    }

    public void setVinNumber(String vinNumber) {
        this.vinNumber = vinNumber;
    }

    @Override
    public int compareTo(Truck truck) {
        return 0;
    }

    @Override
    public String toString() {
        return "Truck{" +
                "truckId=" + truckId +
                ", truckNumber='" + truckNumber + '\'' +
                ", vinNumber='" + vinNumber + '\'' +
                '}';
    }
}
