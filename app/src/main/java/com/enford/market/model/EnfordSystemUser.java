package com.enford.market.model;

import android.os.Parcel;
import android.os.Parcelable;

public class EnfordSystemUser implements Parcelable {

    private Integer id;

    private String username;

    private String password;

    private String email;

    private String name;

    private Integer orgId;

    private Integer roleId;

    private Integer type;

    private Integer deptId;

    private String orgName;

    private String deptName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username == null ? null : username.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email == null ? null : email.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Integer getOrgId() {
        return orgId;
    }

    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getDeptId() {
        return deptId;
    }

    public void setDeptId(Integer deptId) {
        this.deptId = deptId;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.username);
        dest.writeString(this.password);
        dest.writeString(this.email);
        dest.writeString(this.name);
        dest.writeValue(this.orgId);
        dest.writeValue(this.roleId);
        dest.writeValue(this.type);
        dest.writeValue(this.deptId);
        dest.writeString(this.orgName);
        dest.writeString(this.deptName);
    }

    public EnfordSystemUser() {
    }

    protected EnfordSystemUser(Parcel in) {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.username = in.readString();
        this.password = in.readString();
        this.email = in.readString();
        this.name = in.readString();
        this.orgId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.roleId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.type = (Integer) in.readValue(Integer.class.getClassLoader());
        this.deptId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.orgName = in.readString();
        this.deptName = in.readString();
    }

    public static final Parcelable.Creator<EnfordSystemUser> CREATOR = new Parcelable.Creator<EnfordSystemUser>() {
        public EnfordSystemUser createFromParcel(Parcel source) {
            return new EnfordSystemUser(source);
        }

        public EnfordSystemUser[] newArray(int size) {
            return new EnfordSystemUser[size];
        }
    };
}