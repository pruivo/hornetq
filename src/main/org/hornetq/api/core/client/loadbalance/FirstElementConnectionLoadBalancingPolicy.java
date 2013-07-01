/*
 * Copyright 2009 Red Hat, Inc.
 * Red Hat licenses this file to you under the Apache License, version
 * 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *    http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */

package org.hornetq.api.core.client.loadbalance;


import org.hornetq.api.core.Pair;
import org.hornetq.api.core.TransportConfiguration;

/**
 * A {@link ConnectionLoadBalancingPolicy#select(org.hornetq.api.core.Pair[])} always returns 0.
 *
 * @author <a href="mailto:tim.fox@jboss.com">Tim Fox</a>
 *
 * Created 28 Nov 2008 10:56:59
 */
public class FirstElementConnectionLoadBalancingPolicy implements ConnectionLoadBalancingPolicy
{
    @Override
    public int select(Pair<TransportConfiguration, TransportConfiguration>[] elements) {
        return 0;
    }

    public int select(TransportConfiguration[] elements)
   {
      return 0;
   }
}
