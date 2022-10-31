
File sourceFile = new File("src/EntitlementDiscovery.groovy");
Class groovyClass = new GroovyClassLoader(getClass().getClassLoader()).parseClass(sourceFile);
GroovyObject myObject = (GroovyObject) groovyClass.newInstance();

myObject.init("http://localhost:8080/openidm",
                  "openidm-admin",
                   "openidm-admin",
                    "ADGCP",
                    "group",
                    "ldapGroups",
                    "managedUser_systemAdgcpAccount",
                    false,
                    false)
myObject.processEntitlements()