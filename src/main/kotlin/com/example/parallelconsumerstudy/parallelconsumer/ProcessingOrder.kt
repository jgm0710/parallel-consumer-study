package com.example.parallelconsumerstudy.parallelconsumer

/**
 * Enum class representing the processing order of Kafka records.
 */
enum class ProcessingOrder {

    /**
     * Order by key.
     *
     * This strategy is useful when the processing order needs to be consistent with
     * the order in which events occur based on the key values of the Kafka records.
     * It ensures that records with the same key are processed in the order they were produced.
     *
     * @author Jeong-gumin
     * @since 2023/12/04
     */
    KEY,

    /**
     * Order by partition.
     *
     * Sorts records according to the partition value of the Kafka record.
     * This approach is similar to the standard Kafka consumer partition-based
     * processing strategy. Generally, ordering by key is recommended over this
     * method, unless partition-based ordering is specifically needed.
     *
     * @author Jeong-gumin
     * @since 2023/12/04
     */
    PARTITION,

    /**
     * Unordered.
     *
     * Does not specify an order for processing records.
     * Records are assigned to threads randomly and processed in the order they
     * are consumed, which might be the most recent first. This approach is not
     * recommended for scenarios where the order of events is crucial, as it does
     * not guarantee any order of processing.
     *
     * @author Jeong-gumin
     * @since 2023/12/04
     */
    UNORDERED,
}
