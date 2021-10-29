package jp.co.soramitsu.feature_crowdloan_impl.domain.contribute.custom.moonbeam

import jp.co.soramitsu.common.R
import jp.co.soramitsu.common.base.BaseException
import jp.co.soramitsu.common.data.network.HttpExceptionHandler
import jp.co.soramitsu.common.resources.ResourceManager
import jp.co.soramitsu.fearless_utils.extensions.toHexString
import jp.co.soramitsu.feature_account_api.domain.interfaces.AccountRepository
import jp.co.soramitsu.feature_account_api.domain.interfaces.signWithAccount
import jp.co.soramitsu.feature_crowdloan_impl.data.network.api.moonbeam.MoonbeamApi
import jp.co.soramitsu.feature_crowdloan_impl.presentation.contribute.custom.model.CustomContributePayload
import jp.co.soramitsu.runtime.extrinsic.FeeEstimator
import retrofit2.HttpException
import java.io.IOException
import java.math.BigInteger
import java.security.MessageDigest

class MoonbeamContributeInteractor(
    private val moonbeamApi: MoonbeamApi,
    private val httpExceptionHandler: HttpExceptionHandler,
    private val resourceManager: ResourceManager,
    val fearlessReferralCode: String,
    private val feeEstimator: FeeEstimator,
    private val accountRepository: AccountRepository,
) {
    private val digest = MessageDigest.getInstance("SHA-256")

    private var termsHash: String? = null
    private var termsSigned: String? = null
    private var remark: String? = null

    fun nextStep(payload: CustomContributePayload) {
    }

    suspend fun getSystemRemarkFee(): BigInteger {
        return feeEstimator.estimateFee(
            accountAddress = accountRepository.getSelectedAccount().address,
            formExtrinsic = {
                call(
                    moduleName = "System",
                    callName = "remark",
                    arguments = mapOf(
                        "remark" to remark!!.toByteArray()
                    )
                )
            })
    }

    suspend fun getHealth(apiKey: String) = try {
        moonbeamApi.getHealth(apiKey)
        true
    } catch (e: Throwable) {
        val errorCode = (e as? HttpException)?.response()?.code()
        if (errorCode == 403) {
            false
        } else {
            throw transformException(e)
        }
    }

    suspend fun getTerms(): String {
        return httpExceptionHandler.wrap { moonbeamApi.getTerms() }.also {
            calcHashes(digest.digest(it.encodeToByteArray()))
        }
    }

    private suspend fun calcHashes(termsBytes: ByteArray) {
        val account = accountRepository.getSelectedAccount()
        termsHash = termsBytes.toHexString(false)
        termsSigned = accountRepository.signWithAccount(account, termsHash?.encodeToByteArray()!!).toHexString(true)
    }

    private fun transformException(exception: Throwable): BaseException {
        return when (exception) {
            is HttpException -> {
                val response = exception.response()!!

                val errorCode = response.code()
                response.errorBody()?.close()

                BaseException.httpError(errorCode, resourceManager.getString(R.string.common_undefined_error_message))
            }
            is IOException -> BaseException.networkError(resourceManager.getString(R.string.connection_error_message), exception)
            else -> BaseException.unexpectedError(exception)
        }
    }

}
