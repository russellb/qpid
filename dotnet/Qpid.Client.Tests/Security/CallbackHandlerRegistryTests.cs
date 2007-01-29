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
using NUnit.Framework;
using Qpid.Client.Security;


namespace Qpid.Client.Tests.Security
{
   [TestFixture]
   public class CallbackRegistryHandlerTests
   {
      [Test]
      public void ParsesConfiguration()
      {
         CallbackHandlerRegistry registry = CallbackHandlerRegistry.Instance;
         Assert.AreEqual(3, registry.Mechanisms.Length);
         Assert.Contains("TEST", registry.Mechanisms);

         Type handlerType = registry.GetCallbackHandler("TEST");
         Assert.IsNotNull(handlerType);
         Assert.AreEqual(typeof(TestCallbackHandler), handlerType);
      }
   } // class CallbackRegistryHandlerTests

   public class TestCallbackHandler : IAMQCallbackHandler
   {
      public void Initialize(Qpid.Client.Protocol.AMQProtocolSession session)
      {
      }
      public void Handle(Qpid.Sasl.ISaslCallback[] callbacks)
      {
      }

   } // class TestCallbackHandler

} // namespace Qpid.Client.Tests.Connection
