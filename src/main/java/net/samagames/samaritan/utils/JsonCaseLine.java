package net.samagames.samaritan.utils;

/**
 * This file is a part of the SamaGames Project CodeBase
 * This code is absolutely confidential.
 * Created by Thog
 * (C) Copyright Elydra Network 2014 & 2015
 * All rights reserved.
 */
public class JsonCaseLine
{
    private String addedBy;
    private String type;
    private String motif;
    private String duration;
    private Long timestamp;
    private Long durationTime;

    public Long getTimestamp()
    {
        return this.timestamp;
    }

    public void setTimestamp(Long timestamp)
    {
        this.timestamp = timestamp;
    }

    public String getAddedBy()
    {
        return addedBy;
    }

    public void setAddedBy(String addedBy)
    {
        this.addedBy = addedBy;
    }

    public String getType()
    {
        return this.type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getMotif()
    {
        return this.motif;
    }

    public void setMotif(String motif)
    {
        this.motif = motif;
    }

    public String getDuration()
    {
        return this.duration;
    }

    public void setDuration(String duration)
    {
        this.duration = duration;
    }

    public Long getDurationTime()
    {
        return this.durationTime;
    }

    public void setDurationTime(Long durationTime)
    {
        this.durationTime = durationTime;
    }
}
