package com.forgerock.governance
/**
 *
 */
@Grapes(
        @Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.7.1')
)
@Grab(group='com.github.groovy-wslite', module='groovy-wslite', version='1.1.3')
import net.sf.json.JSONNull
import net.sf.json.groovy.JsonSlurper

import wslite.rest.*

class EntitlementDiscovery {
    String OPENIDMUSER = null
    String OPENDIDMPASSWORD = null
    String pgCookie = null
    boolean createrole = false
    boolean createassignment = false
    String mappingName = null
    String nativeAttribute = null
    String appName = null
    String appObjectName = null
    String IDMURL = null
    RESTClient client = null
    static String assignmentPath = "/managed/assignment"
    static String rolePath = "/managed/role?_fields=_id"
    def jsonSlurper = new JsonSlurper()

    def init(String baseURL, String uid,String pwd, String app,String appobject,String nativeattr,String mapping,boolean role,boolean assignment){
        IDMURL = baseURL
        OPENIDMUSER = uid
        OPENDIDMPASSWORD = pwd
        appName = app
        appObjectName = appobject
        nativeAttribute = nativeattr
        mappingName = mapping
        createrole = role
        createassignment = assignment
        client = new RESTClient(IDMURL)
        client.httpClient.sslTrustAllCerts = true

        println "Inited"
    }

    def processEntitlements(){
        def entpath = "/system/"+appName+"/"+appObjectName+"?_queryFilter=true&_pageSize=10&_totalPagedResultsPolicy=EXACT"
        println entpath
        do {
            def res = getObjects(entpath,pgCookie)
            def jsonMap = jsonSlurper.parseText(res.getContentAsString())
            if(!JSONNull.getInstance().equals(jsonMap.pagedResultsCookie)){
                pgCookie = jsonMap.pagedResultsCookie
            } else {
                pgCookie = null
            }

            def result = jsonMap.findAll { it.value instanceof List }
                    .values()
                    .flatten()
                    .collect { [it._id, it.dn, it.samAccountName] }
            result.each {
                def roleName = it[2].toString()
                def roleDesc = it[1].toString()
                def roleType = "Entitlement"
                if (createrole) {
                    def newrole = createRole(roleName, roleDesc, roleType)
                } else {
                    println roleName
                }
            }
        } while (pgCookie != null)
    }

    def getObjects(String epath,String pageCookie){
        // println path
        if(pageCookie != null){
            epath = epath + "&_pagedResultsCookie=" + pageCookie
        }
        def response = client.get(path: epath,
                headers: ['X-OpenIDM-Username': OPENIDMUSER,
                          "X-OpenIDM-Password": OPENDIDMPASSWORD,
                          "Accept-API-Version": "resource=1.0"])
        return response
    }
    /**
     *
     * @param roleName
     * @param roleDescription
     * @param roleType
     * @return
     */
    def createRole(String roleName, String roleDescription, String roleType) {
        def response = client.post (path: rolePath,
                headers: ['X-OpenIDM-Username': OPENIDMUSER,
                          "X-OpenIDM-Password": OPENDIDMPASSWORD,
                          "Accept-API-Version": "resource=1.0"]) {
            type ContentType.JSON
            json name: roleName,
                    description: roleDescription,
                    type: roleType,
                    appname:appName,
                    attribute: nativeAttribute,
                    objectname: appObjectName
        }
        def roleid = response.json._id
        if(createassignment){
            createAssignment(roleid, roleName, roleDescription)
        }
        return roleid
    }

    /**
     *
     * @param roleId
     * @param roleName
     * @param attributeValue
     */
    def createAssignment(String roleId, String roleName,String attributeValue){
        def response = client.post (path: assignmentPath,
                headers: ['X-OpenIDM-Username': OPENIDMUSER,
                          "X-OpenIDM-Password": OPENDIDMPASSWORD,
                          "Accept-API-Version": "resource=1.0"]) {
            type ContentType.JSON
            json    name : roleName + "_Assignment",
                    description: "Assignment for " + roleName + " role.",
                    mapping : mappingName,
                    "attributes": [
                            [
                                    name: nativeAttribute,
                                    value: [
                                            attributeValue
                                    ],
                                    "assignmentOperation" : "mergeWithTarget",
                                    "unassignmentOperation" : "removeFromTarget"
                            ]
                    ],
                    "roles" : [
                            [
                                    "_ref" : "managed/role/" + roleId
                            ]
                    ]
        }
    }

    def boolean validate (){
        return true
    }
}