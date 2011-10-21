all: linux.build

linux.build:
	@echo "=::=::=::= Setting up for Linux =::=::=::= "
	mkdir -p $@
	cd $@ && eval "`rpm --eval "%{cmake}" | grep -v -e "^%"`" -DCMAKE_BUILD_TYPE=RelWithDebInfo ..
	@$(MAKE) --no-print-dir -C $@

.PHONY: linux.build
