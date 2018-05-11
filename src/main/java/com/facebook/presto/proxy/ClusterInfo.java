/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.facebook.presto.proxy;

public class ClusterInfo
{
    private long runningQueries;
    private long blockedQueries;
    private long queuedQueries;
    private long activeWorkers;
    private long runningDrivers;
    private long reservedMemory;
    private long totalInputRows;
    private long totalInputBytes;
    private long totalCpuTimeSecs;

    public long getRunningQueries()
    {
        return runningQueries;
    }

    public void setRunningQueries(long runningQueries)
    {
        this.runningQueries = runningQueries;
    }

    public long getBlockedQueries()
    {
        return blockedQueries;
    }

    public void setBlockedQueries(long blockedQueries)
    {
        this.blockedQueries = blockedQueries;
    }

    public long getQueuedQueries()
    {
        return queuedQueries;
    }

    public void setQueuedQueries(long queuedQueries)
    {
        this.queuedQueries = queuedQueries;
    }

    public long getActiveWorkers()
    {
        return activeWorkers;
    }

    public void setActiveWorkers(long activeWorkers)
    {
        this.activeWorkers = activeWorkers;
    }

    public long getRunningDrivers()
    {
        return runningDrivers;
    }

    public void setRunningDrivers(long runningDrivers)
    {
        this.runningDrivers = runningDrivers;
    }

    public long getReservedMemory()
    {
        return reservedMemory;
    }

    public void setReservedMemory(long reservedMemory)
    {
        this.reservedMemory = reservedMemory;
    }

    public long getTotalInputRows()
    {
        return totalInputRows;
    }

    public void setTotalInputRows(long totalInputRows)
    {
        this.totalInputRows = totalInputRows;
    }

    public long getTotalInputBytes()
    {
        return totalInputBytes;
    }

    public void setTotalInputBytes(long totalInputBytes)
    {
        this.totalInputBytes = totalInputBytes;
    }

    public long getTotalCpuTimeSecs()
    {
        return totalCpuTimeSecs;
    }

    public void setTotalCpuTimeSecs(long totalCpuTimeSecs)
    {
        this.totalCpuTimeSecs = totalCpuTimeSecs;
    }
}
