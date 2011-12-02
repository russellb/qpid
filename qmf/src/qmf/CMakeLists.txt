# QMF library
# Library Version Information (CURRENT.REVISION.AGE):
#
#  CURRENT  => API/ABI version.  Bump this if the interface changes
#  REVISION => Version of underlying implementation.
#              Bump if implementation changes but API/ABI doesn't
#  AGE      => Number of API/ABI versions this is backward compatible with
set (qmf2_version 1.0.0)

include_directories(.. ../include)

if(NOT WIN32)
    set (qmf2_HEADERS
        ../include/qmf/AddressParser.h
        ../include/qmf/AgentEvent.h
        ../include/qmf/Agent.h
        ../include/qmf/AgentSession.h
        ../include/qmf/ConsoleEvent.h
        ../include/qmf/ConsoleSession.h
        ../include/qmf/DataAddr.h
        ../include/qmf/Data.h
        ../include/qmf/exceptions.h
        ../include/qmf/Handle.h
        ../include/qmf/ImportExport.h
        ../include/qmf/posix/EventNotifier.h
        ../include/qmf/Query.h
        ../include/qmf/Schema.h
        ../include/qmf/SchemaId.h
        ../include/qmf/SchemaMethod.h
        ../include/qmf/SchemaProperty.h
        ../include/qmf/SchemaTypes.h
        ../include/qmf/Subscription.h
        )

    set (qmf2_SOURCES
        ${qmf2_HEADERS}
        agentCapability.h
        AddressParser.cpp
        Agent.cpp
        AgentEvent.cpp
        AgentEventImpl.h
        AgentImpl.h
        AgentSession.cpp
        AgentSubscription.cpp
        AgentSubscription.h
        ConsoleEvent.cpp
        ConsoleEventImpl.h
        ConsoleSession.cpp
        ConsoleSessionImpl.h
        constants.cpp
        constants.h
        DataAddr.cpp
        DataAddrImpl.h
        Data.cpp
        DataImpl.h
        EventNotifierImpl.h
        EventNotifierImpl.cpp
        PosixEventNotifier.cpp
        PosixEventNotifierImpl.cpp
        exceptions.cpp
        Expression.cpp
        Expression.h
        Hash.cpp
        Hash.h
        PrivateImplRef.h
        Query.cpp
        QueryImpl.h
        Schema.cpp
        SchemaCache.cpp
        SchemaCache.h
        SchemaId.cpp
        SchemaIdImpl.h
        SchemaImpl.h
        SchemaMethod.cpp
        SchemaMethodImpl.h
        SchemaProperty.cpp
        SchemaPropertyImpl.h
        Subscription.cpp
        SubscriptionImpl.h
        )

    add_library(qmf2 SHARED ${qmf2_SOURCES})
    target_link_libraries(qmf2 qpidmessaging qpidtypes qpidclient qpidcommon)
    set_target_properties(qmf2 PROPERTIES VERSION ${qmf2_version})

    install(TARGETS qmf2 DESTINATION lib${LIB_SUFFIX})
    install(FILES ${qmf2_HEADERS} DESTINATION include/qmf)
endif (NOT WIN32)
