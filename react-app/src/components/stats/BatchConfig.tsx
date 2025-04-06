interface BatchConfigProps {
    config: {
        bufferCapacity: number;
        producerItems: number;
    };
    visible: boolean;
}

const BatchConfig = ({config, visible}: BatchConfigProps) => {
    return (
        <div id="batch-config"
             className="mb-6 p-4 bg-black/[0.03] dark:bg-white/[0.05] shadow-light dark:shadow-dark rounded-sm">
            <h3 id="batch-config-heading" className="mb-2 text-base text-light dark:text-dark">Current Batch
                Configuration</h3>

            <div className="h-2"></div>

            <div
                id="batch-config-summary"
                className="bg-success/10 p-3 rounded-sm border-l-3 border-success dark:border-success-dark"
                style={{visibility: visible ? 'visible' : 'hidden'}}
            >
                <div className="font-mono text-sm text-light-secondary dark:text-dark-secondary leading-relaxed">Buffer
                    Capacity: <span id="buffer-capacity" className="font-semibold">{config.bufferCapacity*3 || '-'}</span>
                </div>
                <div
                    className="font-mono text-sm text-light-secondary dark:text-dark-secondary leading-relaxed">Producer
                    Items: <span id="producer-items" className="font-semibold">{config.producerItems*3 || '-'}</span>
                </div>
            </div>
        </div>
    );
};

export default BatchConfig;
