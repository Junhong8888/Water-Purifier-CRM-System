package CRMv_final;

public class WaterPurifier {
    private String modelName;
    private String installationDate;

    public WaterPurifier() {

    }

    public WaterPurifier(String modelName, String installationDate) {
        this.modelName = modelName;
        this.installationDate = installationDate;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getInstallationDate() {
        return installationDate;
    }

    public void setInstallationDate(String installationDate) {
        this.installationDate = installationDate;
    }

    @Override
    public String toString() {
        return modelName + "," + installationDate;
    }
}