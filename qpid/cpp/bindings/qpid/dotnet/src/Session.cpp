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
#include "Address.h"
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

    // unmanaged clone
    Session::Session(const ::qpid::messaging::Session & session, 
                     Org::Apache::Qpid::Messaging::Connection ^ connRef) :
        parentConnectionp(connRef)
    {
        System::Exception ^ newException = nullptr;

        try 
		{
            sessionp = new ::qpid::messaging::Session (session);
        } 
        catch (const ::qpid::types::Exception & error) 
		{
            String ^ errmsg = gcnew String(error.what());
            newException    = gcnew QpidException(errmsg);
        }

		if (newException != nullptr) 
		{
	        throw newException;
		}
    }


    // Destructor
    Session::~Session()
    {
        this->!Session();
    }


    // Finalizer
    Session::!Session()
    {
        msclr::lock lk(this);

        if (NULL != sessionp)
        {
            delete sessionp;
            sessionp = NULL;
        }
    }


    // Copy constructor look-alike (C#)
    Session::Session(const Session ^ session)
        : parentConnectionp(session->parentConnectionp)
    {
        System::Exception ^ newException = nullptr;

        try 
		{
            sessionp = new ::qpid::messaging::Session(
                        *(const_cast<Session ^>(session)->NativeSession));
          
        } 
        catch (const ::qpid::types::Exception & error) 
		{
            String ^ errmsg = gcnew String(error.what());
            newException    = gcnew QpidException(errmsg);
        }

		if (newException != nullptr) 
		{
	        throw newException;
		}
    }

    // Copy constructor implicitly dereferenced (C++)
    Session::Session(const Session % session)
        : parentConnectionp(session.parentConnectionp)
    {
        System::Exception ^ newException = nullptr;

        try 
		{
            sessionp = new ::qpid::messaging::Session(
                        *(const_cast<Session %>(session).NativeSession));
          
        } 
        catch (const ::qpid::types::Exception & error) 
		{
            String ^ errmsg = gcnew String(error.what());
            newException    = gcnew QpidException(errmsg);
        }

		if (newException != nullptr) 
		{
	        throw newException;
		}
    }


    void Session::Close()
    {
        System::Exception ^ newException = nullptr;

        try 
		{
            sessionp->close();
        } 
        catch (const ::qpid::types::Exception & error) 
		{
            String ^ errmsg = gcnew String(error.what());
            newException    = gcnew QpidException(errmsg);
        }

		if (newException != nullptr) 
		{
	        throw newException;
		}
    }

    void Session::Commit()
    {
        System::Exception ^ newException = nullptr;

        try 
		{
            sessionp->commit();
        } 
        catch (const ::qpid::types::Exception & error) 
		{
            String ^ errmsg = gcnew String(error.what());
            newException    = gcnew QpidException(errmsg);
        }

		if (newException != nullptr) 
		{
	        throw newException;
		}
    }

    void Session::Rollback()
    {
        System::Exception ^ newException = nullptr;

        try 
		{
            sessionp->rollback();
        } 
        catch (const ::qpid::types::Exception & error) 
		{
            String ^ errmsg = gcnew String(error.what());
            newException    = gcnew QpidException(errmsg);
        }

		if (newException != nullptr) 
		{
	        throw newException;
		}
    }

    void Session::Acknowledge()
    {
        Acknowledge(false);
    }

    void Session::Acknowledge(bool sync)
    {
        System::Exception ^ newException = nullptr;

        try 
		{
            sessionp->acknowledge(sync);
        } 
        catch (const ::qpid::types::Exception & error) 
		{
            String ^ errmsg = gcnew String(error.what());
            newException    = gcnew QpidException(errmsg);
        }

		if (newException != nullptr) 
		{
	        throw newException;
		}
    }

    void Session::Acknowledge(Message ^ message)
    {
        Acknowledge(message, false);
    }

    void Session::Acknowledge(Message ^ message, bool sync)
    {
        System::Exception ^ newException = nullptr;

        try 
		{
            sessionp->acknowledge(*(message->NativeMessage), sync);
        } 
        catch (const ::qpid::types::Exception & error) 
		{
            String ^ errmsg = gcnew String(error.what());
            newException    = gcnew QpidException(errmsg);
        }

		if (newException != nullptr) 
		{
	        throw newException;
		}
    }

    void Session::AcknowledgeUpTo(Message ^ message)
    {
        AcknowledgeUpTo(message, false);
    }

    void Session::AcknowledgeUpTo(Message ^ message, bool sync)
    {
        System::Exception ^ newException = nullptr;

        try 
		{
            sessionp->acknowledgeUpTo(*(message->NativeMessage), sync);
        } 
        catch (const ::qpid::types::Exception & error) 
		{
            String ^ errmsg = gcnew String(error.what());
            newException    = gcnew QpidException(errmsg);
        }

		if (newException != nullptr) 
		{
	        throw newException;
		}
    }

    void Session::Reject(Message ^ message)
    {
        System::Exception ^ newException = nullptr;

        try 
		{
            sessionp->::qpid::messaging::Session::reject(*(message->NativeMessage));
        } 
        catch (const ::qpid::types::Exception & error) 
		{
            String ^ errmsg = gcnew String(error.what());
            newException    = gcnew QpidException(errmsg);
        }

		if (newException != nullptr) 
		{
	        throw newException;
		}
    }

    void Session::Release(Message ^ message)
    {
        System::Exception ^ newException = nullptr;

        try 
		{
            sessionp->::qpid::messaging::Session::release(*(message->NativeMessage));
        } 
        catch (const ::qpid::types::Exception & error) 
		{
            String ^ errmsg = gcnew String(error.what());
            newException    = gcnew QpidException(errmsg);
        }

		if (newException != nullptr) 
		{
	        throw newException;
		}
    }

    void Session::Sync()
    {
        Sync(true);
    }

    void Session::Sync(bool block)
    {
        System::Exception ^ newException = nullptr;

        try 
		{
            sessionp->sync(block);
        } 
        catch (const ::qpid::types::Exception & error) 
		{
            String ^ errmsg = gcnew String(error.what());
            newException    = gcnew QpidException(errmsg);
        }

		if (newException != nullptr) 
		{
	        throw newException;
		}
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

            ::qpid::messaging::Receiver receiver = sessionp->::qpid::messaging::Session::nextReceiver(dur);

            Receiver ^ newRcvr = gcnew Receiver(receiver, this);

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
        Sender                     ^ newSender    = nullptr;

        try
        {
            // create the sender
            ::qpid::messaging::Sender sender = 
				sessionp->::qpid::messaging::Session::createSender(QpidMarshal::ToNative(address));

            // create a managed sender
            newSender = gcnew Sender(sender, this);
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
            }
        }
        if (newException != nullptr) 
		{
	        throw newException;
		}

        return newSender;
    }


    Sender ^ Session::CreateSender  (Address ^ address)
    {
        System::Exception          ^ newException = nullptr;
        Sender                     ^ newSender    = nullptr;

        try
        {
            // allocate a native sender
            ::qpid::messaging::Sender sender = 
				sessionp->::qpid::messaging::Session::createSender(*(address->NativeAddress));

            // create a managed sender
            newSender = gcnew Sender(sender, this);
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
        Receiver                    ^ newReceiver  = nullptr;

        try 
		{
            // create the receiver
            ::qpid::messaging::Receiver receiver = 
				sessionp->createReceiver(QpidMarshal::ToNative(address));

            // create a managed receiver
            newReceiver = gcnew Receiver(receiver, this);
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
            }
        }
        if (newException != nullptr) 
		{
	        throw newException;
		}

        return newReceiver;
    }


	Receiver ^ Session::CreateReceiver(Address ^ address)
    {
        System::Exception           ^ newException = nullptr;
        Receiver                    ^ newReceiver  = nullptr;

        try 
		{
            // create the receiver
            ::qpid::messaging::Receiver receiver =
				sessionp->createReceiver(*(address->NativeAddress));

            // create a managed receiver
            newReceiver = gcnew Receiver(receiver, this);
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
        System::Exception           ^ newException = nullptr;
        Sender                      ^ newSender    = nullptr;

        try
        {
            ::qpid::messaging::Sender senderp = 
				sessionp->::qpid::messaging::Session::getSender(QpidMarshal::ToNative(name));

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
            }
        }
        if (newException != nullptr) 
		{
	        throw newException;
		}

        return newSender;
    }



    Receiver ^ Session::GetReceiver(System::String ^ name)
    {
        System::Exception           ^ newException = nullptr;
        Receiver                    ^ newReceiver  = nullptr;

        try
        {
            ::qpid::messaging::Receiver receiver = 
                sessionp->::qpid::messaging::Session::getReceiver(QpidMarshal::ToNative(name));

            newReceiver = gcnew Receiver(receiver, this);
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
            }
        }
        if (newException != nullptr) 
		{
	        throw newException;
		}

        return newReceiver;
    }



    void Session::CheckError()
    {
        System::Exception ^ newException = nullptr;

        try 
		{
            sessionp->checkError();
        } 
        catch (const ::qpid::types::Exception & error) 
		{
            String ^ errmsg = gcnew String(error.what());
            newException    = gcnew QpidException(errmsg);
        }

		if (newException != nullptr) 
		{
	        throw newException;
		}
    }
}}}}
