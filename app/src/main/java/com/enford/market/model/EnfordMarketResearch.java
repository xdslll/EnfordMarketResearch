package com.enford.market.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;

public class EnfordMarketResearch implements Parcelable {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column enford_market_research.id
     *
     * @mbggenerated
     */
    private Integer id;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column enford_market_research.name
     *
     * @mbggenerated
     */
    private String name;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column enford_market_research.create_by
     *
     * @mbggenerated
     */
    private Integer createBy;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column enford_market_research.create_dt
     *
     * @mbggenerated
     */
    @JSONField (format="yyyy-MM-dd")
    private Date createDt;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column enford_market_research.start_dt
     *
     * @mbggenerated
     */
    @JSONField(format="yyyy-MM-dd")
    private Date startDt;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column enford_market_research.end_dt
     *
     * @mbggenerated
     */
    @JSONField(format="yyyy-MM-dd")
    private Date endDt;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column enford_market_research.exe_store_id
     *
     * @mbggenerated
     */
    private Integer exeStoreId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column enford_market_research.res_store_id
     *
     * @mbggenerated
     */
    private Integer resStoreId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column enford_market_research.import_id
     *
     * @mbggenerated
     */
    private Integer importId;

    private String createUsername;

    private String cityName;

    private String exeStoreName;

    private String resStoreName;

    private int state;

    private String stateDesp;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column enford_market_research.id
     *
     * @return the value of enford_market_research.id
     *
     * @mbggenerated
     */
    public Integer getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column enford_market_research.id
     *
     * @param id the value for enford_market_research.id
     *
     * @mbggenerated
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column enford_market_research.name
     *
     * @return the value of enford_market_research.name
     *
     * @mbggenerated
     */
    public String getName() {
        return name;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column enford_market_research.name
     *
     * @param name the value for enford_market_research.name
     *
     * @mbggenerated
     */
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column enford_market_research.create_by
     *
     * @return the value of enford_market_research.create_by
     *
     * @mbggenerated
     */
    public Integer getCreateBy() {
        return createBy;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column enford_market_research.create_by
     *
     * @param createBy the value for enford_market_research.create_by
     *
     * @mbggenerated
     */
    public void setCreateBy(Integer createBy) {
        this.createBy = createBy;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column enford_market_research.create_dt
     *
     * @return the value of enford_market_research.create_dt
     *
     * @mbggenerated
     */
    public Date getCreateDt() {
        return createDt;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column enford_market_research.create_dt
     *
     * @param createDt the value for enford_market_research.create_dt
     *
     * @mbggenerated
     */
    public void setCreateDt(Date createDt) {
        this.createDt = createDt;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column enford_market_research.start_dt
     *
     * @return the value of enford_market_research.start_dt
     *
     * @mbggenerated
     */
    public Date getStartDt() {
        return startDt;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column enford_market_research.start_dt
     *
     * @param startDt the value for enford_market_research.start_dt
     *
     * @mbggenerated
     */
    public void setStartDt(Date startDt) {
        this.startDt = startDt;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column enford_market_research.end_dt
     *
     * @return the value of enford_market_research.end_dt
     *
     * @mbggenerated
     */
    public Date getEndDt() {
        return endDt;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column enford_market_research.end_dt
     *
     * @param endDt the value for enford_market_research.end_dt
     *
     * @mbggenerated
     */
    public void setEndDt(Date endDt) {
        this.endDt = endDt;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column enford_market_research.exe_store_id
     *
     * @return the value of enford_market_research.exe_store_id
     *
     * @mbggenerated
     */
    public Integer getExeStoreId() {
        return exeStoreId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column enford_market_research.exe_store_id
     *
     * @param exeStoreId the value for enford_market_research.exe_store_id
     *
     * @mbggenerated
     */
    public void setExeStoreId(Integer exeStoreId) {
        this.exeStoreId = exeStoreId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column enford_market_research.res_store_id
     *
     * @return the value of enford_market_research.res_store_id
     *
     * @mbggenerated
     */
    public Integer getResStoreId() {
        return resStoreId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column enford_market_research.res_store_id
     *
     * @param resStoreId the value for enford_market_research.res_store_id
     *
     * @mbggenerated
     */
    public void setResStoreId(Integer resStoreId) {
        this.resStoreId = resStoreId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column enford_market_research.import_id
     *
     * @return the value of enford_market_research.import_id
     *
     * @mbggenerated
     */
    public Integer getImportId() {
        return importId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column enford_market_research.import_id
     *
     * @param importId the value for enford_market_research.import_id
     *
     * @mbggenerated
     */
    public void setImportId(Integer importId) {
        this.importId = importId;
    }

    public String getCreateUsername() {
        return createUsername;
    }

    public void setCreateUsername(String createUsername) {
        this.createUsername = createUsername;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getExeStoreName() {
        return exeStoreName;
    }

    public void setExeStoreName(String exeStoreName) {
        this.exeStoreName = exeStoreName;
    }

    public String getResStoreName() {
        return resStoreName;
    }

    public void setResStoreName(String resStoreName) {
        this.resStoreName = resStoreName;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getStateDesp() {
        return stateDesp;
    }

    public void setStateDesp(String stateDesp) {
        this.stateDesp = stateDesp;
    }


    public EnfordMarketResearch() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.name);
        dest.writeValue(this.createBy);
        dest.writeLong(createDt != null ? createDt.getTime() : -1);
        dest.writeLong(startDt != null ? startDt.getTime() : -1);
        dest.writeLong(endDt != null ? endDt.getTime() : -1);
        dest.writeValue(this.exeStoreId);
        dest.writeValue(this.resStoreId);
        dest.writeValue(this.importId);
        dest.writeString(this.createUsername);
        dest.writeString(this.cityName);
        dest.writeString(this.exeStoreName);
        dest.writeString(this.resStoreName);
        dest.writeInt(this.state);
        dest.writeString(this.stateDesp);
    }

    protected EnfordMarketResearch(Parcel in) {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.name = in.readString();
        this.createBy = (Integer) in.readValue(Integer.class.getClassLoader());
        long tmpCreateDt = in.readLong();
        this.createDt = tmpCreateDt == -1 ? null : new Date(tmpCreateDt);
        long tmpStartDt = in.readLong();
        this.startDt = tmpStartDt == -1 ? null : new Date(tmpStartDt);
        long tmpEndDt = in.readLong();
        this.endDt = tmpEndDt == -1 ? null : new Date(tmpEndDt);
        this.exeStoreId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.resStoreId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.importId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.createUsername = in.readString();
        this.cityName = in.readString();
        this.exeStoreName = in.readString();
        this.resStoreName = in.readString();
        this.state = in.readInt();
        this.stateDesp = in.readString();
    }

    public static final Creator<EnfordMarketResearch> CREATOR = new Creator<EnfordMarketResearch>() {
        public EnfordMarketResearch createFromParcel(Parcel source) {
            return new EnfordMarketResearch(source);
        }

        public EnfordMarketResearch[] newArray(int size) {
            return new EnfordMarketResearch[size];
        }
    };
}