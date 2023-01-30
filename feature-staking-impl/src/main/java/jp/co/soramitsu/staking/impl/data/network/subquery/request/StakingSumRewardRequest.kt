package jp.co.soramitsu.staking.impl.data.network.subquery.request

class StakingSumRewardRequest(accountAddress: String) {
    val query = """
    query {
        historyElements(
        orderBy: TIMESTAMP_DESC,
        filter: {
            reward: { notEqualTo: "null"},
            address: { equalTo: "$accountAddress"}  }
        ) {
            nodes {
                  reward
            }
        }
    }
    """.trimIndent()
}
