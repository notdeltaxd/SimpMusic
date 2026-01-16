package com.maxrave.simpmusic.utils

import com.maxrave.simpmusic.expect.md5Hash
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/**
 * Last.fm API helper for authentication flow.
 * Based on BloomeeTunes implementation.
 * 
 * Flow:
 * 1. Call fetchRequestToken() to get a token
 * 2. Open getAuthUrl(token) in browser for user to authorize
 * 3. After user authorizes, call fetchSessionKey(token) to get session key
 */
object LastFmAuthHelper {
    
    private const val API_URL = "https://ws.audioscrobbler.com/2.0/"
    
    private val json = Json { ignoreUnknownKeys = true }
    
    // Create a simple HttpClient for Last.fm API calls
    private val httpClient by lazy {
        HttpClient {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }
    }
    
    /**
     * Generate API signature using MD5 hash.
     * Params are sorted alphabetically, concatenated, then apiSecret is appended.
     */
    fun generateApiSig(params: Map<String, String>, apiSecret: String): String {
        val sortedParams = params.keys.sorted()
        val sortedParamsString = sortedParams.joinToString("") { key -> "$key${params[key]}" }
        val toHash = "$sortedParamsString$apiSecret"
        return md5Hash(toHash)
    }
    
    /**
     * Step 1: Fetch a request token from Last.fm API.
     * Returns the token string.
     */
    suspend fun fetchRequestToken(apiKey: String, apiSecret: String): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val params = mutableMapOf(
                    "method" to "auth.getToken",
                    "api_key" to apiKey
                )
                val apiSig = generateApiSig(params, apiSecret)
                
                val response = httpClient.get(API_URL) {
                    parameter("method", "auth.getToken")
                    parameter("api_key", apiKey)
                    parameter("api_sig", apiSig)
                    parameter("format", "json")
                }
                
                val responseText = response.bodyAsText()
                val jsonResponse = json.parseToJsonElement(responseText).jsonObject
                
                if (jsonResponse.containsKey("token")) {
                    val token = jsonResponse["token"]?.jsonPrimitive?.content
                        ?: return@withContext Result.failure(Exception("Token is null"))
                    Result.success(token)
                } else {
                    val error = jsonResponse["message"]?.jsonPrimitive?.content ?: "Unknown error"
                    Result.failure(Exception(error))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * Step 2: Get the auth URL for user to open in browser.
     */
    fun getAuthUrl(apiKey: String, token: String): String {
        return "https://www.last.fm/api/auth/?api_key=$apiKey&token=$token"
    }
    
    /**
     * Step 3: Fetch session key after user has authorized in browser.
     * Returns a pair of (username, sessionKey).
     */
    suspend fun fetchSessionKey(
        apiKey: String,
        apiSecret: String,
        token: String
    ): Result<Pair<String, String>> {
        return withContext(Dispatchers.IO) {
            try {
                val params = mutableMapOf(
                    "method" to "auth.getSession",
                    "api_key" to apiKey,
                    "token" to token
                )
                val apiSig = generateApiSig(params, apiSecret)
                
                val response = httpClient.get(API_URL) {
                    parameter("method", "auth.getSession")
                    parameter("api_key", apiKey)
                    parameter("token", token)
                    parameter("api_sig", apiSig)
                    parameter("format", "json")
                }
                
                val responseText = response.bodyAsText()
                val jsonResponse = json.parseToJsonElement(responseText).jsonObject
                
                if (jsonResponse.containsKey("session")) {
                    val session = jsonResponse["session"]?.jsonObject
                    val name = session?.get("name")?.jsonPrimitive?.content
                        ?: return@withContext Result.failure(Exception("Username is null"))
                    val key = session["key"]?.jsonPrimitive?.content
                        ?: return@withContext Result.failure(Exception("Session key is null"))
                    Result.success(Pair(name, key))
                } else {
                    val error = jsonResponse["message"]?.jsonPrimitive?.content ?: "Failed to get session"
                    Result.failure(Exception(error))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}

