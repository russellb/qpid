/*MGEN:commentPrefix=//*/
#ifndef _MANAGEMENT_/*MGEN:Class.NameUpper*/_
#define _MANAGEMENT_/*MGEN:Class.NameUpper*/_

//
// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
// 
//   http://www.apache.org/licenses/LICENSE-2.0
// 
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.
//

/*MGEN:Root.Disclaimer*/

#include "qpid/management/ManagementObject.h"

namespace qpid {
    namespace management {
        class ManagementAgent;
    }
}

namespace qmf {
/*MGEN:Class.OpenNamespaces*/

class /*MGEN:Class.NameCap*/ : public ::qpid::management::ManagementObject
{
  private:

    static std::string packageName;
    static std::string className;
    static uint8_t     md5Sum[MD5_LEN];
/*MGEN:IF(Class.ExistOptionals)*/
    uint8_t presenceMask[/*MGEN:Class.PresenceMaskBytes*/];
/*MGEN:Class.PresenceMaskConstants*/
/*MGEN:ENDIF*/

    // Properties
/*MGEN:Class.ConfigDeclarations*/
    // Statistics
/*MGEN:Class.InstDeclarations*/
/*MGEN:IF(Class.ExistPerThreadStats)*/
    // Per-Thread Statistics
    struct PerThreadStats {
/*MGEN:Class.PerThreadDeclarations*/
    };

    struct PerThreadStats** perThreadStatsArray;

    inline struct PerThreadStats* getThreadStats() {
        int idx = getThreadIndex();
        struct PerThreadStats* threadStats = perThreadStatsArray[idx];
        if (threadStats == 0) {
            threadStats = new(PerThreadStats);
            perThreadStatsArray[idx] = threadStats;
/*MGEN:Class.InitializePerThreadElements*/
        }
        return threadStats;
    }

    void aggregatePerThreadStats(struct PerThreadStats*) const;
/*MGEN:ENDIF*/
  public:
    static void writeSchema(std::string& schema);
    void mapEncodeValues(::qpid::types::Variant::Map& map,
                         bool includeProperties=true,
                         bool includeStatistics=true);
    void mapDecodeValues(const ::qpid::types::Variant::Map& map);
    void doMethod(std::string&           methodName,
                  const ::qpid::types::Variant::Map& inMap,
                  ::qpid::types::Variant::Map& outMap,
                  const std::string& userId);
    std::string getKey() const;
/*MGEN:IF(Root.GenQMFv1)*/
    uint32_t writePropertiesSize() const;
    void readProperties(const std::string& buf);
    void writeProperties(std::string& buf) const;
    void writeStatistics(std::string& buf, bool skipHeaders = false);
    void doMethod(std::string& methodName,
                  const std::string& inBuf,
                  std::string& outBuf,
                  const std::string& userId);
/*MGEN:ENDIF*/

    writeSchemaCall_t getWriteSchemaCall() { return writeSchema; }
/*MGEN:IF(Class.NoStatistics)*/
    // Stub for getInstChanged.  There are no statistics in this class.
    bool getInstChanged() { return false; }
    bool hasInst() { return false; }
/*MGEN:ENDIF*/

    /*MGEN:Class.NameCap*/(::qpid::management::ManagementAgent* agent,
                            ::qpid::management::Manageable* coreObject/*MGEN:Class.ParentArg*//*MGEN:Class.ConstructorArgs*/);
    ~/*MGEN:Class.NameCap*/();

    /*MGEN:Class.SetGeneralReferenceDeclaration*/

    static void registerSelf(::qpid::management::ManagementAgent* agent);
    std::string& getPackageName() const { return packageName; }
    std::string& getClassName() const { return className; }
    uint8_t* getMd5Sum() const { return md5Sum; }

    // Method IDs
/*MGEN:Class.MethodIdDeclarations*/
    // Accessor Methods
/*MGEN:Class.AccessorMethods*/
};

}/*MGEN:Class.CloseNamespaces*/

#endif  /*!_MANAGEMENT_/*MGEN:Class.NameUpper*/_*/
