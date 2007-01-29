/*
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

using System;
using System.Collections;
using System.Text;

using Qpid.Sasl.Mechanisms;

namespace Qpid.Sasl
{
   public class DefaultClientFactory : ISaslClientFactory
   {
      private static readonly string[] SUPPORTED = new string[] { 
               DigestSaslClient.Mechanism,
               CramMD5SaslClient.Mechanism,
               PlainSaslClient.Mechanism, 
               AnonymousSaslClient.Mechanism,
            };

      public string[] GetSupportedMechanisms(IDictionary props)
      {
         if ( props == null )
            throw new ArgumentNullException("props");

         ArrayList vetoed = new ArrayList();

         if ( props.Contains(SaslProperties.PolicyNoPlainText) ||
             props.Contains(SaslProperties.PolicyNoDictionary) ||
             props.Contains(SaslProperties.PolicyNoActive) ||
             props.Contains(SaslProperties.PolicyForwardSecrecy) ||
             props.Contains(SaslProperties.PolicyPassCredentials) )
         {
            vetoed.Add(CramMD5SaslClient.Mechanism);
            vetoed.Add(PlainSaslClient.Mechanism);
            vetoed.Add(AnonymousSaslClient.Mechanism);
         } 
         if ( props.Contains(SaslProperties.PolicyNoAnonymous) )
         {
            vetoed.Add(AnonymousSaslClient.Mechanism);
         }

         ArrayList available = new ArrayList();
         foreach ( string mech in SUPPORTED )
         {
            if ( !vetoed.Contains(mech) )
               available.Add(mech);
         }
         return (string[])available.ToArray(typeof(string));
      }

      public ISaslClient CreateClient(
         string[] mechanisms, string authorizationId,
         string protocol, string serverName, 
         IDictionary props, ISaslCallbackHandler handler
         )
      {
         foreach ( string mech in mechanisms )
         {
            switch ( mech )
            {
            case PlainSaslClient.Mechanism:
               return new PlainSaslClient(authorizationId, props, handler);
            case CramMD5SaslClient.Mechanism:
               return new CramMD5SaslClient(authorizationId, props, handler);
            case AnonymousSaslClient.Mechanism:
               return new AnonymousSaslClient(authorizationId, props, handler);
            case DigestSaslClient.Mechanism:
               return new DigestSaslClient(authorizationId, serverName, protocol, props, handler);
            }
         }
         // unknown mechanism
         return null;
      }
   } // class DefaultClientFactory

} // namespace Qpid.Sasl

