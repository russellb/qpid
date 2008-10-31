package org.apache.qpid.management;

/**
 * Enumerative interfaces containing all QMan messages.
 * 
 * @author Andrea Gazzarini
 */
public interface Messages 
{	
	// INFO
	String QMAN_000001_STARTING_QMAN = "<QMAN-000001> : Starting Q-Man...";
	String QMAN_000002_READING_CONFIGURATION = "<QMAN-000002> : Reading Q-Man configuration...";
	String QMAN_000003_CREATING_MANAGEMENT_CLIENTS = "<QMAN-000003> : Creating management client(s)...";
	String QMAN_000004_MANAGEMENT_CLIENT_CONNECTED = "<QMAN-000004> : Management client for broker %s successfully connected.";
	String QMAN_000005_TYPE_MAPPING_CONFIGURED = "<QMAN-000005> : Type mapping : code = %s associated to %s (validator class is %s)";
	String QMAN_000006_ACCESS_MODE_MAPPING_CONFIGURED = "<QMAN-000006> : Access Mode mapping : code = %s associated to %s";
	String QMAN_000007_MANAGEMENT_HANDLER_MAPPING_CONFIGURED = "<QMAN-000007> : Management Queue Message Handler Mapping : opcode = %s associated with %s";
	String QMAN_000008_METHOD_REPLY_HANDLER_MAPPING_CONFIGURED  = "<QMAN-000008> : Method-Reply Queue Message Handler Mapping : opcode = %s associated with %s";
	String QMAN_000009_BROKER_DATA_CONFIGURED = "<QMAN-000009> : Broker configuration %s: %s";
	String QMAN_000010_INCOMING_SCHEMA = "<QMAN-000010> : Incoming schema for %s::%s.%s";
	String QMAN_000011_SHUTDOWN_INITIATED = "<QMAN-000011> : The shutdown sequence has been initiated for management client connected with broker %s";
	String QMAN_000012_MANAGEMENT_CLIENT_SHUT_DOWN = "<QMAN-000012> : Management client connected with broker %s shut down successfully.";
	String QMAN_000013_METHOD_REPLY_CONSUMER_INSTALLED = "<QMAN-000013> : Method-reply queue consumer has been successfully installed and bound on broker %s.";
	String QMAN_000014_MANAGEMENT_CONSUMER_INSTALLED ="<QMAN-000014> : Management queue consumer has been successfully installed and bound on broker %s.";
	String QMAN_000015_MANAGEMENT_QUEUE_DECLARED = "<QMAN-000015> : Management queue with name %s has been successfully declared and bound on broker %s.";
	String QMAN_000016_METHOD_REPLY_QUEUE_DECLARED = "<QMAN-000016> : Method-reply queue with name %s has been successfully declared and bound on broker %s.";
	String QMAN_000017_CONSUMER_HAS_BEEN_REMOVED = "<QMAN-000017> : Consumer %s has been removed from broker %s.";
	String QMAN_000018_QUEUE_UNDECLARED = "<QMAN-000018> : Queue %s has been removed from broker %s.";
	String QMAN_000019_QMAN_STARTED = "<QMAN-000019> : Q-Man open for e-business.";
	String QMAN_000020_SHUTTING_DOWN_QMAN = "<QMAN-000020> : Shutting down Q-Man...";
	String QMAN_000021_SHUT_DOWN = "<QMAN-000021> : Q-Man shut down.";
	String QMAN_000022_NO_BROKER_CONFIGURED = "<QMAN-000022> : Q-Man has no configured broker : in order to connect with a running one use Q-Man Administration interface.";
	String QMAN_000023_QMAN_REGISTERED_AS_MBEAN = "<QMAN-000023> : Q-Man service is now available on MBeanServer.";
	
	 // DEBUG
	String QMAN_200001_INCOMING_MESSAGE_HAS_BEEN_RECEIVED = "<QMAN-200001> : New incoming message has been received. Message content is %s";
	String QMAN_200002_OPCODE_HANDLER_ASSOCIATION = "<QMAN-200002> : \"%s\" opcode is associated to handler %s";
	String QMAN_200003_MESSAGE_FORWARDING = "<QMAN-200003> : Incoming message with \"%s\" as opcode will be forwarded to %s for processing.";
	String QMAN_200004_MANAGEMENT_QUEUE_NAME = "<QMAN-200004> : Management queue name : %s"; 
	String QMAN_200005_METHOD_REPLY_QUEUE_NAME = "<QMAN-200005> : Method-reply queue name : %s";
	String QMAN_200006_QPID_CONNECTION_RELEASED = "<QMAN-200006> : Connection %s returned to the pool.";
	String QMAN_200007_TEST_CONNECTION_ON_RESERVE = "<QMAN-200007> : Test connection on reserve. Is valid? %s";
	String QMAN_200008_CONNECTION_DESTROYED = "<QMAN-200008> : Connection has been destroyed.";
	String QMAN_200009_CONNECTION_DESTROY_FAILURE = "<QMAN-200009> : Unable to destroy a connection object.";
	String QMAN_200010_EVENT_MBEAN_REGISTERED = "<QMAN-200010> : Event instance %s::%s::%s successfully registered with MBean Server with name %s";
	String QMAN_200011_OBJECT_MBEAN_REGISTERED = "<QMAN-200011> : Object instance %s::%s::%s:%s successfully registered with MBean Server with name %s";
	String QMAN_200012_OBJECT_MBEAN_UNREGISTERED = "<QMAN-200012> : Object instance %s::%s::%s:%s successfully unregistered from MBean Server. Name was %s";
	String QMAN_200013_ARGUMENT_VALUE_ENCODED = "<QMAN-200013> : Encoded value %S for argument %s. Type is %s";
	String QMAN_200014_INCOMING_INSTRUMENTATION_DATA = "<QMAN-200014> : Incoming instrumentation data for %s::%s.%s.%s";
	String QMAN_200015_INCOMING_CONFIGURATION_DATA = "<QMAN-200015> : Incoming configuration data for %s::%s.%s.%s";
	String QMAN_200016_PROPERTY_DEFINITION_HAS_BEEN_BUILT = "<QMAN-200016> : Property definition for %s::%s.%s has been built.";
	String QMAN_200017_STATISTIC_DEFINITION_HAS_BEEN_BUILT = "<QMAN-200017> : Statistic definition for %s::%s.%s has been built.";
	String QMAN_200018_OPTIONAL_PROPERTIES_INFO = "<QMAN-200018> : Class %s::%s.%s has %s optional properties.";
	String QMAN_200019_EVENT_ARGUMENT_DEFINITION_HAS_BEEN_BUILT = "<QMAN-200019> : Event argument definition for %s::%s.%s has been built.";
	String QMAN_200020_ENTITY_DEFINITION_HAS_BEEN_BUILT = "<QMAN-200020> : Entity definition has been built (without schema) for %s::%s.%s";
	String QMAN_200021_INCOMING_EVENT_DATA = "<QMAN-200021> : Incoming data for event %s::%s.%s";
	String QMAN_200022_VALIDATOR_INSTALLED = "<QMAN-200022> : Validator %s for type %s successfully installed.";
	String QMAN_200023_VALIDATOR_NOT_FOUND = "<QMAN-200023> : No validator was found for type %s. The default (empty) validator will be used.";
	String QMAN_200024_MANAGEMENT_MESSAGE_HAS_BEEN_SENT = "<QMAN-200024> : Message has been sent to management exchange. Message content : %s";
	String QMAN_200025_SUBSCRIPTION_DECLARED = "<QMAN-200025> : New subscription between queue %s and destination %s has been declared.";
	String QMAN_200026_SUBSCRIPTION_REMOVED = "<QMAN-200026> : Subscription named %s has been removed from remote broker.";
	String QMAN_200027_QUEUE_DECLARED = "<QMAN-200027> : New queue with name %s has been declared.";
	String QMAN_200028_QUEUE_REMOVED= "<QMAN-200028> : New queue with name %s has been undeclared.";
	String QMAN_200029_BINDING_DECLARED = "<QMAN-200029> : New binding with %s as routing key has been declared between queue %s and exchange %s.";
	String QMAN_200030_BINDING_REMOVED = "<QMAN-200030> : Binding with %s as routing key has been removed between queue %s and exchange %s.";
	String QMAN_200031_COMPOUND_MESSAGE_CONTAINS = "<QMAN-200031> : Incoming compound message contains %s message(s).";
	String QMAN_200032_COMMAND_MESSAGE_ROUTING_KEY = "<QMAN-200032> : Command message routing key : %s";
	
	// WARNING
    String QMAN_300001_MESSAGE_DISCARDED = "<QMAN-300001> : No handler has been configured for processing messages with \"%s\" as opcode. Message will be discarded.";
	String QMAN_300002_UNKNOWN_SEQUENCE_NUMBER = "<QMAN-300002> : Unable to deal with incoming message because it contains a unknown sequence number (%s).";
    String QMAN_300003_BROKER_ALREADY_CONNECTED = "<QMAN-300003> : Unable to enlist given broker connection data : QMan is already connected with broker %s";
    String QMAN_300004_UNVALID_CONFIGURATION_FILE = "<QMAN-300004> : The given configuration file (%s) is not valid (it doesn't exist or cannot be read)";
	
	// ERROR
	String QMAN_100001_BAD_MAGIC_NUMBER_FAILURE = "<QMAN-100001> : Message processing failure : incoming message contains a bad magic number (%s) and therefore will be discaded.";
	String QMAN_100002_MESSAGE_READ_FAILURE = "<QMAN-100002> : Message I/O failure : unable to read byte message content and therefore it will be discarded.";
	String QMAN_100003_MESSAGE_PROCESS_FAILURE = "<QMAN-100003> : Message processing failure : unknown exception; see logs for more details.";
	String QMAN_100004_HANDLER_INITIALIZATION_FAILURE = "<QMAN-100004> : Message handler configured for opcode %s thrown an exception in initialization and therefore will be discarded.";
	String QMAN_100005_CLASS_SCHEMA_PROCESSING_FAILURE = "<QMAN-100005> : Q-Man was unable to process the schema response message.";
	String QMAN_100006_EVENT_SCHEMA_PROCESSING_FAILURE = "<QMAN-100006> : Q-Man was unable to process the schema response message.";
	String QMAN_100007_UNABLE_TO_CONNECT_WITH_BROKER = "<QMAN-100007> : Unable to connect with broker located on %s. This broker will be ignored.";
	String QMAN_100008_MANAGEMENT_MESSAGE_HANDLER_NOT_AVAILABLE = "<QMAN-100008> : Management Message Handler configured for opcode %s is not available and therefore will be discarded.";
	String QMAN_100009_METHOD_REPLY_MESSAGE_HANDLER_NOT_AVAILABLE = "<QMAN-100009> :Method-Reply Message Handler configured for opcode %s is not available and therefore will be discarded.";
	String QMAN_100010_METHOD_INVOCATION_RESULT_FAILURE = "<QMAN-100010> : an exception occurred while storing the result of a method invocation. Sequence number was %s";
	String QMAN_100011_UNKNOWN_CLASS_KIND = "<QMAN-100011> : Unknwon class kind : %s).";
	String QMAN_100012_SCHEMA_MESSAGE_PROCESSING_FAILURE = "<QMAN-100012> : Q-Man was unable to process the schema response message.";
	String QMAN_100013_MBEAN_REGISTRATION_FAILURE = "<QMAN-100013> : Unable to unregister object instance %s.";
	String QMAN_100014_ATTRIBUTE_DECODING_FAILURE = "<QMAN-100014> : Unable to decode value for attribute %s";
	String QMAN_100015_UNABLE_TO_SEND_SCHEMA_REQUEST = "<QMAN-100015> : Unable to send a schema request schema for %s.%s";
	String QMAN_100016_UNABLE_TO_DECODE_FEATURE_VALUE = "<QMAN-100016> : Unable to decode value for %s::%s::%s";
	String QMAN_100017_UNABLE_TO_CONNECT = "<QMAN-100017>: Cannot connect to broker %s on %s";
	String QMAN_100018_UNABLE_TO_STARTUP_CORRECTLY = "<QMAN-100018> : Q-Man was unable to startup correctly : see logs for further details.";
	
	// MESSAGES
	String EVENT_SEVERITY_ATTRIBUTE_DESCRIPTION = "Severity level for this event.";
	String EVENT_TIMESTAMP_ATTRIBUTE_DESCRIPTION = "Current timestamp of this event.";
}
