package org.apache.maven.surefire.junitcore.pc;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.util.concurrent.Semaphore;

/**
 * @author Tibor Digana (tibor17)
 * @since 2.16
 *
 * @see Balancer
 */
final class ThreadResourcesBalancer implements Balancer
{
    private final Semaphore balancer;
    private final int numPermits;

    /**
     * <tt>fair</tt> set to false.
     *
     * @param numPermits number of permits to acquire when maintaining concurrency on tests.
     *                   Must be &gt;0 and &lt; {@link Integer#MAX_VALUE}.
     *
     * @see #ThreadResourcesBalancer(int, boolean)
     */
    ThreadResourcesBalancer( int numPermits )
    {
        this( numPermits, false );
    }

    /**
     * @param numPermits number of permits to acquire when maintaining concurrency on tests.
     *                   Must be &gt;0 and &lt; {@link Integer#MAX_VALUE}.
     * @param fair <tt>true</tt> guarantees the waiting schedulers to wake up in order they acquired a permit
     */
    ThreadResourcesBalancer( int numPermits, boolean fair )
    {
        balancer = new Semaphore( numPermits, fair );
        this.numPermits = numPermits;
    }

    /**
     * Acquires a permit from this balancer, blocking until one is available.
     *
     * @return <code>true</code> if current thread is <em>NOT</em> interrupted
     *         while waiting for a permit.
     */
    public boolean acquirePermit()
    {
        try
        {
            balancer.acquire();
            return true;
        }
        catch ( InterruptedException e )
        {
            return false;
        }
    }

    /**
     * Releases a permit, returning it to the balancer.
     */
    public void releasePermit()
    {
        balancer.release();
    }

    public void releaseAllPermits()
    {
        balancer.release( numPermits );
    }
}