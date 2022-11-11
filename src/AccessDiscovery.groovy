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
/**
 *
 */
class AccessDiscovery {
    String OPENIDMUSER = null
    String OPENDIDMPASSWORD = null

    String IDMURL = null
    RESTClient client = null
    static String assignmentPath = "/managed/assignment"
    static String rolePath = "/managed/role?_fields=_id"
    def jsonSlurper = new JsonSlurper()

    def init(String baseURL, String uid,String pwd){
        IDMURL = baseURL
        OPENIDMUSER = uid
        OPENDIDMPASSWORD = pwd
        client = new RESTClient(IDMURL)
        client.httpClient.sslTrustAllCerts = true

        println "Inited"
    }

    def getAccountsForApp(String appName, String appAccountObject){
        def accountURL = "system/"+appName+"/"+appAccountObject+"?_queryFilter=true&_pageSize=10&_totalPagedResultsPolicy=EXACT"
        String pgCookie = null
        do {
            def res = getObjects(accountURL,pgCookie)
            def jsonMap = jsonSlurper.parseText(res.getContentAsString())
            if(!JSONNull.getInstance().equals(jsonMap.pagedResultsCookie)){
                pgCookie = jsonMap.pagedResultsCookie
            } else {
                pgCookie = null
            }

            def result = jsonMap.findAll { it.value instanceof List }
                    .values()
                    .flatten()
                    .collect { [it._id, it.dn, it.ldapGroups] }
            result.each {
                def entName = it[2].toString()
                def entDesc = it[1].toString()
                println entName + ":"+entDesc
            }
        } while (pgCookie != null)

    }

    def getEntitlementsForApp(String appName,String entitlementObjectName){
        //roletype+sw+\"Entitlement\" and+appname eq \""+appName + "\" and objectname eq\""+entitlementObjectName+"\"
        def encstr = URLEncoder.encode("roletype sw \"Entitlement\" and appname eq \""+appName + "\" and objectname eq \""+entitlementObjectName+"\"","UTF-8")
        def entitlementURL = "/managed/role?_queryFilter="+encstr+"&_fields=_id,name,description"
        String pgCookie = null
        //println entitlementURL
        do {
            def res = getObjects(entitlementURL,pgCookie)
            def jsonMap = jsonSlurper.parseText(res.getContentAsString())
            if(!JSONNull.getInstance().equals(jsonMap.pagedResultsCookie)){
                pgCookie = jsonMap.pagedResultsCookie
            } else {
                pgCookie = null
            }

            def result = jsonMap.findAll { it.value instanceof List }
                    .values()
                    .flatten()
                    .collect { [it._id, it.name, it.description] }
            result.each {
                def entName = it[2].toString()
                def entDesc = it[1].toString()
                println entName + ":"+entDesc
            }
        } while (pgCookie != null)
    }

    /**
     *
     * @param epath
     * @param pageCookie
     * @return
     */
    def getObjects(String path,String pageCookie){
        // println path
        if(pageCookie != null){
            path = path + "&_pagedResultsCookie=" + pageCookie
        }
        def response = client.get(path: path,
                headers: ['X-OpenIDM-Username': OPENIDMUSER,
                          "X-OpenIDM-Password": OPENDIDMPASSWORD,
                          "Accept-API-Version": "resource=1.0"])
        return response
    }
}


