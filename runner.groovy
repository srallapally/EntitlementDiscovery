import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

OkHttpClient client = new OkHttpClient().newBuilder()
        .build();
MediaType mediaType = MediaType.parse("application/json");
RequestBody body = RequestBody.create(mediaType, "\n{\n    \"_id\": \"968b1339-eef6-42db-9724-4111f403b963\",\n    \"_rev\": \"3f799497-9f6c-4cca-af4a-2ff850ddc125-154\",\n    \"_ref\": \"managed/role/2458dcbe-3989-4bb4-a52f-9cd1497115e4\",\n    \"_refResourceCollection\": \"managed/role\",\n    \"_refResourceId\": \"2458dcbe-3989-4bb4-a52f-9cd1497115e4\",\n    \"_refProperties\": {\n        \"_id\": \"968b1339-eef6-42db-9724-4111f403b963\",\n        \"_rev\": \"3f799497-9f6c-4cca-af4a-2ff850ddc125-154\"\n    }\n}");
Request request = new Request.Builder()
        .url("http://localhost:8080/openidm/managed/user/15a6ec81-f232-4407-964a-130caee5d26a/roles/968b1339-eef6-42db-9724-4111f403b963")
        .method("DELETE", body)
        .addHeader("X-OpenIDM-Username", "openidm-admin")
        .addHeader("X-OpenIDM-Password", "openidm-admin")
        .addHeader("Accept-API-Version", "resource=1.0")
        .addHeader("Content-Type", "application/json")
        .build();
Response response = client.newCall(request).execute();
println response.body().string();
/*
def entDiscovery = "src/EntitlementDiscovery.groovy"
def accessDiscovery = "src/AccessDiscovery.groovy"
File sourceFile = null
Class groovyClass = null
GroovyObject myObject = null

sourceFile = new File(entDiscovery);
groovyClass = new GroovyClassLoader(getClass().getClassLoader()).parseClass(sourceFile);
myObject = (GroovyObject) groovyClass.newInstance();

myObject.init("http://localhost:8080/openidm",
                  "openidm-admin",
                   "openidm-admin",
                    "ADGCP",
                    "group",
                    "ldapGroups",
                    "managedUser_systemAdgcpAccount",
                    true,
                    true)
myObject.processEntitlements()

sourceFile = new File(accessDiscovery);
groovyClass = new GroovyClassLoader(getClass().getClassLoader()).parseClass(sourceFile);
myObject = (GroovyObject) groovyClass.newInstance();
//myObject.init("http://localhost:8080/openidm",
//        "openidm-admin",
//        "openidm-admin")
//myObject.getAccountsForApp("ADGCP","account")
//myObject.getEntitlementsForApp("ADGCP","group")



def map = [name:"Jerry"]
def age = "cn=X,dn=y"
map["age"] = age
def hobbyLiteral = "hobby"
def hobbyMap = ["hobby": "Singing"]
map.putAll(hobbyMap)

println map
*/
/*

def builder = new JsonBuilder()
def _refId = "123"
def _refResourceId1 = "abcd123"
def _refResourceRev1 = "bbbbbbb"
def _refPropId = "2345143545"
def _refPropRev = "x/y"
builder { operation  "remove"
         field  "/roles"
         value     {
                    ref  _refId
                    _refResourceCollection  'managed/role'
                    _refResourceId  _refResourceId1
                    _refResourceRev  _refResourceRev1
                    _refProperties  {
                        _id   _refPropId
                        _rev  _refPropRev
                    }
                }
        }

println builder.toPrettyString()

 */
