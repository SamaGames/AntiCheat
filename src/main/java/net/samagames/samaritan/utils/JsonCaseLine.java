package net.samagames.samaritan.utils;

/*
 * This file is part of AntiCheat (Samaritan).
 *
 * AntiCheat (Samaritan) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * AntiCheat (Samaritan) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with AntiCheat (Samaritan).  If not, see <http://www.gnu.org/licenses/>.
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
