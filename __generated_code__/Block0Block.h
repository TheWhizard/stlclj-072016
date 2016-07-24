#ifndef _Block0BLOCK_H
#define _Block0BLOCK_H

using namespace ExchangeImplementationUtility;
        
class Block0Block : public ABetterRootCombine
{
public:
    Block0Block(commonServices *parmpcs):
        ABetterRootCombine(parmpcs, "Block0")
    {
        init();
    }

    Block0Block(pxDataManager *parmnewpdm, pxDataManager *parmcmppdm, TransactionControlServices *parmptcs, commonServices *parmpcs):
        ABetterRootCombine(parmnewpdm, parmcmppdm, parmptcs, parmpcs, "Block0")
    {
        init();
    }

    ~Block0Block(){};

    void init() {
        setCombineMetadata(&blockConfig_);
    }

private:
    class BlockCombineConfig : public ABetterBaseCombine::ABetterBlockCombineConfig {
        public:
        BlockCombineConfig() {

            static const char* filterFields[] = {"FirstDate", "LastDate", "Source", "Key"};
            ABetterBaseCombine::ABetterBlockCombineConfig::registerFilterFields(filterFields, 4);

            static const char* keyFields[] = {"Source", "Key"};
            ABetterBaseCombine::ABetterBlockCombineConfig::registerKeyFields(keyFields, 2);

            static const char* combineFields[] = {"LastDate", "VersionNumber"};
            ABetterBaseCombine::ABetterBlockCombineConfig::registerCombineFields(combineFields, 2);
        }
    };
    static BlockCombineConfig blockConfig_;
};

#endif /* Block0BLOCK_H */
