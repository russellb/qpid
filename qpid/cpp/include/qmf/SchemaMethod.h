#ifndef QMF_SCHEMA_METHOD_H
#define QMF_SCHEMA_METHOD_H
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

#include "qmf/ImportExport.h"
#include "qpid/sys/IntegerTypes.h"
#include "qmf/Handle.h"
#include "qmf/SchemaTypes.h"
#include <string>

namespace qmf {

#ifndef SWIG
    template <class> class PrivateImplRef;
#endif

    class SchemaMethodImpl;
    class SchemaProperty;

    class QMF_CLASS_EXTERN SchemaMethod : public qmf::Handle<SchemaMethodImpl> {
    public:
        QMF_EXTERN SchemaMethod(SchemaMethodImpl* impl = 0);
        QMF_EXTERN SchemaMethod(const SchemaMethod&);
        QMF_EXTERN SchemaMethod& operator=(const SchemaMethod&);
        QMF_EXTERN ~SchemaMethod();

        QMF_EXTERN SchemaMethod(const std::string&, const std::string& o="");

        QMF_EXTERN void setDesc(const std::string&);
        QMF_EXTERN void addArgument(const SchemaProperty&);

        QMF_EXTERN const std::string& getName() const;
        QMF_EXTERN const std::string& getDesc() const;
        QMF_EXTERN uint32_t getArgumentCount() const;
        QMF_EXTERN SchemaProperty getArgument(uint32_t) const;

        /**
         * Get a method's argument by name
         *
         * \param[in] The name of the argument to look up
         *
         * \note This function is synchronous and non-blocking.  It checks locally
         * cached data and will not send any messages to the remote agent.  Use
         * qmf::Agent::querySchema[Async] to get the latest schema information from
         * the remote agent.
         *
         * \return The SchemaProperty for the given argument name, if found.
         * If it is not found, a SchemaPropert with no Impl will be returned.
         */
        QMF_EXTERN SchemaProperty getArgument(const std::string &arg_name) const;

#ifndef SWIG
    private:
        friend class qmf::PrivateImplRef<SchemaMethod>;
        friend struct SchemaMethodImplAccess;
#endif
    };

}

#endif
