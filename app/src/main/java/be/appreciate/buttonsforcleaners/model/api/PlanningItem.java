package be.appreciate.buttonsforcleaners.model.api;

import android.content.ContentValues;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import be.appreciate.buttonsforcleaners.database.PlanningTable;
import be.appreciate.buttonsforcleaners.utils.DateUtils;

/**
 * Created by Inneke De Clippel on 1/03/2016.
 */
public class PlanningItem
{
    @SerializedName("Id")
    private int id;
    @SerializedName("ContractTypeId")
    private int contractTypeId;
    @SerializedName("Accountancyname")
    private String accountancyName;
    @SerializedName("CustomerName")
    private String customerName;
    @SerializedName("Date")
    private String date;
    @SerializedName("StartTime")
    private String startTime;
    @SerializedName("EndTime")
    private String endTime;
    @SerializedName("LocationName")
    private String locationName;
    @SerializedName("TabletCanOnlyStartExecutionInNeighbourhood")
    private boolean proximityRequired;
    @SerializedName("TaskDescription")
    private String description;
    @SerializedName("PhoneNumber")
    private String phone;
    @SerializedName("WorkInstructions")
    private String instructions;
    @SerializedName("SpecificProducts")
    private List<Product> products;
    @SerializedName("AssignedResources")
    private List<Resource> resources;
    @SerializedName("LocationAddress")
    private Address address;

    public List<Product> getProducts()
    {
        return products;
    }

    public List<Resource> getResources()
    {
        return resources;
    }

    public ContentValues getContentValues()
    {
        ContentValues cv = new ContentValues();

        cv.put(PlanningTable.COLUMN_PLANNING_ID, this.id);
        cv.put(PlanningTable.COLUMN_CONTRACT_TYPE_ID, this.contractTypeId);
        cv.put(PlanningTable.COLUMN_ACCOUNTANCY_NAME, this.accountancyName);
        cv.put(PlanningTable.COLUMN_CUSTOMER_NAME, this.customerName);
        cv.put(PlanningTable.COLUMN_DATE, DateUtils.extractMillis(this.date));
        cv.put(PlanningTable.COLUMN_START_TIME, this.startTime);
        cv.put(PlanningTable.COLUMN_END_TIME, this.endTime);
        cv.put(PlanningTable.COLUMN_LOCATION_NAME, this.locationName);
        cv.put(PlanningTable.COLUMN_PROXIMITY_REQUIRED, this.proximityRequired);
        cv.put(PlanningTable.COLUMN_DESCRIPTION, this.description);
        cv.put(PlanningTable.COLUMN_INSTRUCTIONS, this.instructions);
        cv.put(PlanningTable.COLUMN_PHONE, this.phone);

        if(this.address != null)
        {
            cv.put(PlanningTable.COLUMN_STREET, this.address.getStreet());
            cv.put(PlanningTable.COLUMN_POSTAL_CODE, this.address.getPostalCode());
            cv.put(PlanningTable.COLUMN_CITY, this.address.getCity());
            cv.put(PlanningTable.COLUMN_LATITUDE, this.address.getLatitude());
            cv.put(PlanningTable.COLUMN_LONGITUDE, this.address.getLongitude());
        }

        return  cv;
    }

    public ContentValues[] getProductContentValues()
    {
        ContentValues[] cvArray = new ContentValues[this.products != null ? this.products.size() : 0];

        if(this.products != null)
        {
            for(int i = 0; i < this.products.size(); i++)
            {
                Product product = this.products.get(i);
                cvArray[i] = product.getPlanningContentValues(this.id);
            }
        }

        return cvArray;
    }

    public ContentValues[] getResourceContentValues()
    {
        ContentValues[] cvArray = new ContentValues[this.resources != null ? this.resources.size() : 0];

        if(this.resources != null)
        {
            for(int i = 0; i < this.resources.size(); i++)
            {
                Resource resource = this.resources.get(i);
                cvArray[i] = resource.getContentValues(this.id);
            }
        }

        return cvArray;
    }
}
