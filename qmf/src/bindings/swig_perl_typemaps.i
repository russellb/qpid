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

%wrapper %{

#include <stdarg.h>

    SV* MapToPerl(const qpid::types::Variant::Map*);
    SV* ListToPerl(const qpid::types::Variant::List*);
    void PerlToMap(SV*, qpid::types::Variant::Map*);
    void PerlToList(SV*, qpid::types::Variant::List*);

    qpid::types::Variant PerlToVariant(SV* value) {
        if (SvROK(value)) {
            if (SvTYPE(SvRV(value)) == SVt_PVHV) {
                qpid::types::Variant::Map map;
                PerlToMap(value, &map);
                return qpid::types::Variant(map);
            }
            else if (SvTYPE(SvRV(value)) == SVt_PVAV) {
                qpid::types::Variant::List list;
                PerlToList(value, &list);
                return qpid::types::Variant(list);
            }
        }
        else {
            if (SvIOK(value)) {
                return qpid::types::Variant((int64_t) SvIV(value));
             }
            else if (SvNOK(value)) {
                return qpid::types::Variant((float)SvNV(value));
            }
            else if (SvPOK(value)) {
                return qpid::types::Variant(std::string(SvPV_nolen(value)));
            }
        }
        return qpid::types::Variant();
    }

    SV* VariantToPerl(const qpid::types::Variant* v) {
        SV* result = newSV(0);
        try {
            switch (v->getType()) {
            case qpid::types::VAR_VOID: {
                sv_setiv(result, (IV)0);
                break;
            }
            case qpid::types::VAR_BOOL : {
                result = boolSV(v->asBool());
                break;
            }
            case qpid::types::VAR_UINT8 :
            case qpid::types::VAR_UINT16 :
            case qpid::types::VAR_UINT32 : {
                sv_setuv(result, (UV)v->asUint32());
                break;
            }
            case qpid::types::VAR_UINT64 : {
                sv_setuv(result, (UV)v->asUint64());
                break;
            }
            case qpid::types::VAR_INT8 : 
            case qpid::types::VAR_INT16 :
            case qpid::types::VAR_INT32 : {
                sv_setiv(result, (IV)v->asInt32());
                break;
            }
            case qpid::types::VAR_INT64 : {
                sv_setiv(result, (IV)v->asInt64());
                break;
            }
            case qpid::types::VAR_FLOAT : {
                sv_setnv(result, (double)v->asFloat());
                break;
            }
            case qpid::types::VAR_DOUBLE : {
                sv_setnv(result, (double)v->asDouble());
                break;
            }
            case qpid::types::VAR_STRING : {
                const std::string val(v->asString());
                result = newSVpvn(val.c_str(), val.size());
                break;
            }
            case qpid::types::VAR_MAP : {
                result = MapToPerl(&(v->asMap()));
                break;
            }
            case qpid::types::VAR_LIST : {
                result = ListToPerl(&(v->asList()));
                break;
            }
            case qpid::types::VAR_UUID : {
            }
            }
        } catch (qpid::types::Exception& ex) {
            Perl_croak(aTHX_ ex.what());
        }

        return result;
    }

    SV* MapToPerl(const qpid::types::Variant::Map* map) {
        SV *result = newSV(0);
        HV *hv = (HV *)sv_2mortal((SV *)newHV());
        qpid::types::Variant::Map::const_iterator iter;
        for (iter = map->begin(); iter != map->end(); iter++) {
            const std::string key(iter->first);
            SV* perlval = VariantToPerl(&(iter->second));
            hv_store(hv, key.c_str(), key.size(), perlval, 0);
        }
        SvSetSV(result, newRV_noinc((SV *)hv));
        return result;
    }

    SV* ListToPerl(const qpid::types::Variant::List* list) {
        SV* result = newSV(0);
        AV* av  = (AV *)sv_2mortal((SV *)newAV());
        qpid::types::Variant::List::const_iterator iter;
        for (iter = list->begin(); iter != list->end(); iter++) {
            SV* perlval = VariantToPerl(&(*iter));
            av_push(av, perlval);
        }
        SvSetSV(result, newRV_noinc((SV *)av));
        return result;
    }

    void PerlToMap(SV* hash, qpid::types::Variant::Map* map) {
        map->clear();
        HV* hv = (HV *)SvRV(hash);
        HE* he;
        while((he = hv_iternext(hv)) != NULL) {
            SV* svkey = HeSVKEY_force(he);
            SV* svval = HeVAL(he);
            (*map)[std::string(SvPV_nolen(svkey))] = PerlToVariant(svval);
        }
    }

    void PerlToList(SV* ary, qpid::types::Variant::List* list) {
        list->clear();
        AV * av = (AV *)SvRV(ary);
        I32 len = av_len(av) + 1;
        if (len > 0) {
            for (I32 i = 0; i < len; i++) {
                list->push_back(PerlToVariant(*av_fetch(av, i, 0)));
            }
        }
    }
%}

%typemap (in) void * {
    $1 = (void *)SvIV($input);
}

%typemap (out) void * {
    sv_setiv($result, (IV)$1);
    argvi++;
}

%typemap (in) uint16_t, uint32_t, uint64_t {
    if (SvIOK($input)) {
        $1 = ($1_ltype)SvUV($input);
    }
    else {
        SWIG_exception_fail(SWIG_ValueError, "not an integer");
    }
}

%typemap (out) uint16_t, uint32_t, uint64_t {
    sv_setuv($result, (UV)$1);
    argvi++;
}

%typemap (in) int32_t, int64_t {
    if (SvIOK($input)) {
        $1 = ($1_ltype)SvIV($input);
    }
    else {
        SWIG_exception_fail(SWIG_ValueError, "not an integer");
    }
}

%typemap (out) int32_t, int64_t {
    sv_setiv($result, (IV)$1);
    argvi++;
}

%typemap(in) bool {
    $1 = (bool)SvTRUE($input);
}

%typemap (out) bool {
    $result = boolSV($1);
    argvi++;
}


%typemap (typecheck, precedence=SWIG_TYPECHECK_UINT64) uint64_t {
    $1 = SvIOK($input) ? 1 : 0;
}

%typemap (typecheck, precedence=SWIG_TYPECHECK_UINT32) uint32_t {
    $1 = SvIOK($input) ? 1 : 0;
}


/*
 * Variant types: C++ --> Perl
 */
%typemap(out) qpid::types::Variant::Map {
    $result = MapToPerl(&$1);
    argvi++;
}

%typemap(out) qpid::types::Variant::Map& {
    $result = MapToPerl($1);
    argvi++;
}

%typemap(out) qpid::types::Variant::List {
    $result = ListToPerl(&$1);
    argvi++;
}

%typemap(out) qpid::types::Variant::List& {
    $result = ListToPerl($1);
    argvi++;
}

%typemap(out) qpid::types::Variant& {
    $result = VariantToPerl($1);
    argvi++;
}


/*
 * Variant types: Perl --> C++
 */
%typemap(in) qpid::types::Variant& {
    $1 = new qpid::types::Variant(PerlToVariant($input));
}

%typemap(in) qpid::types::Variant::Map& {
    $1 = new qpid::types::Variant::Map();
    PerlToMap($input, $1);

}

%typemap(in) qpid::types::Variant::List& {
    $1 = new qpid::types::Variant::List();
    PerlToList($input, $1);

}

%typemap(in) const qpid::types::Variant::Map const & {
    $1 = new qpid::types::Variant::Map();
    PerlToMap($input, $1);
}

%typemap(in) const qpid::types::Variant::List const & {
    $1 = new qpid::types::Variant::List();
    PerlToList($input, $1);
}

%typemap(freearg) qpid::types::Variant& {
    delete $1;
}

%typemap(freearg) qpid::types::Variant::Map& {
    delete $1;
}

%typemap(freearg) qpid::types::Variant::List& {
    delete $1;
}


/*
 * Variant types: typecheck maps
 */
%typemap(typecheck) qpid::types::Variant::Map& {
    $1 = (SvTYPE(SvRV($input)) == SVt_PVHV) ? 1 : 0;
}

%typemap(typecheck) qpid::types::Variant::List& {
    $1 = (SvTYPE(SvRV($input)) == SVt_PVAV) ? 1 : 0;
}

%typemap(typecheck) qpid::types::Variant& {
    $1 = (SvIOK($input) ||
          SvNOK($input) ||
          SvPOK($input) ) ? 1 : 0;
}

%typemap(typecheck) const qpid::types::Variant::Map const & {
    $1 = (SvTYPE(SvRV($input)) == SVt_PVHV) ? 1 : 0;
}

%typemap(typecheck) const qpid::types::Variant::List const & {
    $1 = (SvTYPE(SvRV($input)) == SVt_PVAV) ? 1 : 0;
}

%typemap(typecheck) const qpid::types::Variant const & {
    $1 = (SvIOK($input) ||
          SvNOK($input) ||
          SvPOK($input) ) ? 1 : 0;
}

/* No boolean type for perl. 
   Boolean is simply and integer in perl
*/
%typecheck(SWIG_TYPECHECK_BOOL) bool {
    $1 = (SvIOK($input)) ? 1 : 0;
}
