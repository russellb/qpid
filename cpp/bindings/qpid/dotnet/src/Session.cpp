/*
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
*/

#include <windows.h>
#include <msclr\lock.h>
#include <oletx2xa.h>
#include <string>
#include <limits>

#include "qpid/messaging/Session.h"
#include "qpid/messaging/exceptions.h"

#include "QpidMarshal.h"
#include "Session.h"
#include "Connection.h"
#include "Duration.h"
#include "Receiver.h"
#include "Sender.h"
#include "Message.h"
#include "QpidException.h"

namespace Org {
namespace Apache {
namespace Qpid {
namespace Messaging {

    /// <summary>
    /// Session is a managed wrapper for a ::qpid::messaging::Session
    /// </summary>

    // constructor
    Session::Session(::qpid::messaging::Session * sp, Connection ^ connRef) :
        sessionp(sp),
        parentConnectionp(connRef)
    {
    }


    // Destructor
    Session::~Session()
    {
        Cleanup();
    }


    // Finalizer
    Session::!Session()
    {
        Cleanup();
    }

    // copy constructor
    Session::Session(const Session % rhs)
    {
        sessionp = rhs.sessionp;
        parentConnectionp = rhs.parentConnectionp;
    }


    // Destroys kept object
    // TODO: add lock
    void Session::Cleanup()
    {
        if (NULL != sessionp)
        {
            delete sessionp;
            sessionp = NULL;
        }
    }

    void Session::Close()
    {
        sessionp->close();
    }

    void Session::Commit()
    {
        sessionp->commit();
    }

    void Session::Rollback()
    {
        sessionp->rollback();
    }

    void Session::Acknowledge()
    {
        Acknowledge(false);
    }

    void Session::Acknowledge(bool sync)
    {
        sessionp->acknowledge(sync);
    }

    void Session::Reject(Message ^ message)
    {
        sessionp->::qpid::messaging::Session::reject(*(message->NativeMessage));
    }

    void Session::Release(Message ^ message)
    {
        sessionp->::qpid::messaging::Session::release(*(message->NativeMessage));
    }

    void Session::Sync()
    {
        Sync(true);
    }

    void Session::Sync(bool block)
    {
        sessionp->sync(block);
    }

    // next(receiver)
    bool Session::NextReceiver(Receiver ^ rcvr)
    {
        return NextReceiver(rcvr, DurationConstants::FORVER);
    }

    bool Session::NextReceiver(Receiver ^ rcvr, Duration ^ timeout)
    {
        System::Exception           ^ newException = nullptr;

        try 
		{
			// create a duration object
            ::qpid::messaging::Duration dur(timeout->Milliseconds);

			// wait for the next received message
            return sessionp->nextReceiver(*(rcvr->NativeReceiver), dur);
        } 
        catch (const ::qpid::types::Exception & error) 
		{
            String ^ errmsg = gcnew String(error.what());
            if ("No message to fetch" == errmsg){
                return false;
            }
            newException    = gcnew QpidException(errmsg);
        }

		if (newException != nullptr) 
		{
	        throw newException;
		}

        return true;
    }

    // receiver = next()
    Receiver ^ Session::NextReceiver()
    {
        return NextReceiver(DurationConstants::FORVER);
    }

    Receiver ^ Session::NextReceiver(Duration ^ timeout)
    {
        System::Exception           ^ newException = nullptr;

        try
        {
            ::qpid::messaging::Duration dur(timeout->Milliseconds);
            ::qpid::messaging::Receiver * rcvr = new ::qpid::messaging::Receiver;

            *rcvr = sessionp->::qpid::messaging::Session::nextReceiver(dur);

            Receiver ^ newRcvr = gcnew Receiver(rcvr, this);

            return newRcvr;
        } 
        catch (const ::qpid::types::Exception & error) 
        {
            String ^ errmsg = gcnew String(error.what());
            if ("No message to fetch" == errmsg)
            {
                return nullptr;
            }
            newException    = gcnew QpidException(errmsg);
        }

		if (newException != nullptr) 
		{
	        throw newException;
		}

		return nullptr;
    }


    Sender ^ Session::CreateSender  (System::String ^ address)
    {
        System::Exception          ^ newException = nullptr;
        ::qpid::messaging::Sender  * senderp         = NULL;
        Sender                     ^ newSender       = nullptr;

        try
        {
            // allocate a native sender
            ::qpid::messaging::Sender * senderp = new ::qpid::messaging::Sender ;

            // create the sender
            *senderp = sessionp->::qpid::messaging::Session::createSender(QpidMarshal::ToNative(address));

            // create a managed sender
            newSender = gcnew Sender(senderp, this);
        } 
        catch (const ::qpid::types::Exception & error) 
        {
            String ^ errmsg = gcnew String(error.what());
            newException    = gcnew QpidException(errmsg);
        }
        finally
        {
            if (newException != nullptr)
            {
				if (newSender != nullptr)
				{
					delete newSender;
				}
				else
				{
					if (senderp != NULL)
					{
						delete senderp;
					}
				}
            }
        }
        if (newException != nullptr) 
		{
	        throw newException;
		}

        return newSender;
    }

    Receiver ^ Session::CreateReceiver(System::String ^ address)
    {
        System::Exception           ^ newException = nullptr;
        ::qpid::messaging::Receiver * receiverp    = NULL;
        Receiver                    ^ newReceiver  = nullptr;

        try 
		{
            // allocate a native receiver
            receiverp = new ::qpid::messaging::Receiver;

            // create the receiver
            *receiverp = sessionp->createReceiver(QpidMarshal::ToNative(address));

            // create a managed receiver
            newReceiver = gcnew Receiver(receiverp, this);
        } 
        catch (const ::qpid::types::Exception & error) 
		{
            String ^ errmsg = gcnew String(error.what());
            newException    = gcnew QpidException(errmsg);
        }
        finally 
		{
            if (newException != nullptr)
			{
				if (newReceiver != nullptr)
				{
					delete newReceiver;
				}
				else
				{
					if (receiverp != NULL)
					{
						delete receiverp;
					}
				}
            }
        }
        if (newException != nullptr) 
		{
	        throw newException;
		}

        return newReceiver;
    }


    Receiver ^ Session::CreateReceiver()
    {
        System::Exception           ^ newException = nullptr;
        ::qpid::messaging::Receiver * receiverp    = NULL;
        Receiver                    ^ newReceiver  = nullptr;

        try
        {
            // allocate a native receiver
            receiverp = new ::qpid::messaging::Receiver;

            // create a managed receiver
            newReceiver = gcnew Receiver(receiverp, this);
        } 
        catch (const ::qpid::types::Exception & error) 
        {
            String ^ errmsg = gcnew String(error.what());
            newException    = gcnew QpidException(errmsg);
        }
        finally 
		{
            if (newException != nullptr)
			{
				if (newReceiver != nullptr)
				{
					delete newReceiver;
				}
				else
				{
					if (receiverp != NULL)
					{
						delete receiverp;
					}
				}
            }
        }
        if (newException != nullptr) 
		{
	        throw newException;
		}

        return newReceiver;
    }


    Sender ^ Session::GetSender(System::String ^ name)
    {
        ::qpid::messaging::Sender * sender = new ::qpid::messaging::Sender;

        *sender = sessionp->::qpid::messaging::Session::getSender(QpidMarshal::ToNative(name));

        Sender ^ newSender = gcnew Sender(sender, this);

        return newSender;
    }



    Receiver ^ Session::GetReceiver(System::String ^ name)
    {
        ::qpid::messaging::Receiver * receiver = new ::qpid::messaging::Receiver;

        *receiver = sessionp->::qpid::messaging::Session::getReceiver(QpidMarshal::ToNative(name));

        Receiver ^ newReceiver = gcnew Receiver(receiver, this);

        return newReceiver;
    }



    Connection ^ Session::GetConnection()
    {
        return parentConnectionp;
    }

    void Session::CheckError()
    {
        sessionp->checkError();
    }
}}}}