DOXYGEN:=$(shell which doxygen 2>/dev/null)
DOT:=$(shell which dot 2>/dev/null)

all: linux.build

linux.build:
	@echo "=::=::=::= Setting up for Linux =::=::=::= "
	mkdir -p $@
	cd $@ && eval "`rpm --eval "%{cmake}" | grep -v -e "^%"`" -DCMAKE_BUILD_TYPE=RelWithDebInfo ..
	@$(MAKE) --no-print-dir -C $@

doxygen:
ifeq ($(DOXYGEN),)
	@echo
	@echo "***********************************************"
	@echo "***                                         ***"
	@echo "*** You do not have doxygen installed.      ***"
	@echo "*** Please install it before generating the ***"
	@echo "*** developer documentation.                ***"
	@echo "***                                         ***"
	@echo "***********************************************"
	@exit 1
endif
ifeq ($(DOT),)
	@echo
	@echo "***********************************************"
	@echo "***                                         ***"
	@echo "*** You do not have graphviz installed.     ***"
	@echo "*** Please install it before generating the ***"
	@echo "*** developer documentation.                ***"
	@echo "***                                         ***"
	@echo "***********************************************"
	@exit 1
endif
	@doxygen doc/Doxyfile

.PHONY: linux.build doxygen
