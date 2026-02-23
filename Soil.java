public class Soil {
    private String type;
    private Plant plant;
    private Fertilizer fertilizer;
    private boolean isMeteoriteTile;
    private boolean isExcavated;

    public Soil(String type) {
        this.type = type;
        this.plant = null;
        this.fertilizer = null;
        this.isMeteoriteTile = false;
        this.isExcavated = false;
    }

    public boolean hasPlant() {
        return plant != null;
    }

    public boolean hasFertilizer() {
        return fertilizer != null;
    }

    public void setMeteoriteTile(boolean value) {
        this.isMeteoriteTile = value;
    }

    public void excavate() {
        this.isMeteoriteTile = false;
        this.isExcavated = true;
    }

    public String getType() {
        return type;
    }

    public Plant getPlant() {
        return plant;
    }

    public void setPlant(Plant plant) {
        this.plant = plant;
    }

    public Fertilizer getFertilizer() {
        return fertilizer;
    }

    public void setFertilizer(Fertilizer fertilizer) {
        this.fertilizer = fertilizer;
    }

    public boolean isMeteoriteTile() {
        return isMeteoriteTile;
    }

    public boolean isExcavated() {
        return isExcavated;
    }
}