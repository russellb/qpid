#ifndef QMF_SCHEMA_H
#define QMF_SCHEMA_H
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

#include <qmf/ImportExport.h>
#include "qpid/sys/IntegerTypes.h"
#include "qmf/Handle.h"
#include "qmf/SchemaTypes.h"
#include <string>

namespace qmf {

#ifndef SWIG
    template <class> class PrivateImplRef;
#endif

    class SchemaImpl;
    class SchemaId;
    class SchemaProperty;
    class SchemaMethod;

    class QMF_CLASS_EXTERN Schema : public qmf::Handle<SchemaImpl> {
    public:
        QMF_EXTERN Schema(SchemaImpl* impl = 0);
        QMF_EXTERN Schema(const Schema&);
        QMF_EXTERN Schema& operator=(const Schema&);
        QMF_EXTERN ~Schema();

        QMF_EXTERN Schema(int, const std::string&, const std::string&);
        QMF_EXTERN const SchemaId& getSchemaId() const;

        QMF_EXTERN void finalize();
        QMF_EXTERN bool isFinalized() const;
        QMF_EXTERN void addProperty(const SchemaProperty&);
        QMF_EXTERN void addMethod(const SchemaMethod&);
        QMF_EXTERN void setDesc(const std::string&);
        QMF_EXTERN const std::string& getDesc() const;

        QMF_EXTERN void setDefaultSeverity(int);
        QMF_EXTERN int getDefaultSeverity() const;

        QMF_EXTERN uint32_t getPropertyCount() const;
        QMF_EXTERN SchemaProperty getProperty(uint32_t) const;

        /**
         * Get a schema property by name
         *
         * \param[in] property_name The name of the property to look up
         *
         * \note This function is synchronous and non-blocking.  It checks locally
         * cached data and will not send any messages to the remote agent.  Use
         * qmf::Agent::querySchema[Async] to get the latest schema information from
         * the remote agent.
         *
         * \return The SchemaProperty with the given name, if found.  If it is
         * not found, a SchemaProperty with no Impl will be returned.
         */
        QMF_EXTERN SchemaProperty getProperty(const std::string &property_name) const;

        QMF_EXTERN uint32_t getMethodCount() const;
        QMF_EXTERN SchemaMethod getMethod(uint32_t) const;

        /**
         * Get a schema method by name
         *
         * \param[in] method_name The name of the method to look up
         *
         * \note This function is synchronous and non-blocking.  It checks locally
         * cached data and will not send any messages to the remote agent.  Use
         * qmf::Agent::querySchema[Async] to get the latest schema information from
         * the remote agent.
         *
         * \return The SchemaMethod with the given name, if found.  If it is
         * not found, a SchemaMethod with no Impl will be returned.
         */
        QMF_EXTERN SchemaMethod getMethod(const std::string &method_name) const;

#ifndef SWIG
    private:
        friend class qmf::PrivateImplRef<Schema>;
        friend struct SchemaImplAccess;
#endif
    };

}

#endif
