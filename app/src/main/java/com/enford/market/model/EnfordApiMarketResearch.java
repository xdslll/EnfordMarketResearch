package com.enford.market.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author xiads
 * @date 16/2/12
 */
public class EnfordApiMarketResearch implements Parcelable {

    EnfordMarketResearch research;

    EnfordProductDepartment dept;

    EnfordProductCompetitors comp;

    int codCount;

    int haveFinished;

    public EnfordMarketResearch getResearch() {
        return research;
    }

    public void setResearch(EnfordMarketResearch research) {
        this.research = research;
    }

    public EnfordProductDepartment getDept() {
        return dept;
    }

    public void setDept(EnfordProductDepartment dept) {
        this.dept = dept;
    }

    public EnfordProductCompetitors getComp() {
        return comp;
    }

    public void setComp(EnfordProductCompetitors comp) {
        this.comp = comp;
    }

    public int getCodCount() {
        return codCount;
    }

    public void setCodCount(int codCount) {
        this.codCount = codCount;
    }

    public int getHaveFinished() {
        return haveFinished;
    }

    public void setHaveFinished(int haveFinished) {
        this.haveFinished = haveFinished;
    }

    public EnfordApiMarketResearch() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.research, 0);
        dest.writeParcelable(this.dept, 0);
        dest.writeParcelable(this.comp, 0);
        dest.writeInt(this.codCount);
        dest.writeInt(this.haveFinished);
    }

    protected EnfordApiMarketResearch(Parcel in) {
        this.research = in.readParcelable(EnfordMarketResearch.class.getClassLoader());
        this.dept = in.readParcelable(EnfordProductDepartment.class.getClassLoader());
        this.comp = in.readParcelable(EnfordProductCompetitors.class.getClassLoader());
        this.codCount = in.readInt();
        this.haveFinished = in.readInt();
    }

    public static final Creator<EnfordApiMarketResearch> CREATOR = new Creator<EnfordApiMarketResearch>() {
        public EnfordApiMarketResearch createFromParcel(Parcel source) {
            return new EnfordApiMarketResearch(source);
        }

        public EnfordApiMarketResearch[] newArray(int size) {
            return new EnfordApiMarketResearch[size];
        }
    };
}
