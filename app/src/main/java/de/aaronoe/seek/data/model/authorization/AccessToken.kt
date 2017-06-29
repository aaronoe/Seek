package de.aaronoe.seek.data.model.authorization

import com.google.gson.annotations.SerializedName

/**
 * Created by private on 29.06.17.
 *
 */
data class AccessToken(@SerializedName("access_token") val accessToken: String,
                       @SerializedName("token_type") val tokenType: String,
                       @SerializedName("scope") val scope: String,
                       @SerializedName("created_at") val createdAt: Long)