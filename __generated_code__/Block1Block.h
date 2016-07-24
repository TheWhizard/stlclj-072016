#ifndef Block1BLOCK_H
#define Block1BLOCK_H

using namespace ExchangeImplementationUtility;

class Block1Block  : public ABetterChildCombine
{
public:
    Block1Block(commonServices *parmpcs, CXBlock* parentBlock):
        ABetterChildCombine(parmpcs, parentBlock, "Block1")
    {
        init();
    }

    Block1Block(pxDataManager *parmnewpdm, pxDataManager *parmcmppdm, TransactionControlServices *parmptcs, commonServices *parmpcs, CXBlock* parentBlock):
        ABetterChildCombine(parmnewpdm, parmcmppdm, parmptcs, parmpcs, parentBlock, "Block1")
    {
        init();
    }

    ~Block1Block(){};

    void init() {
        setCombineMetadata(&Block1Block::blockConfig_);
    }

private:
    class BlockCombineConfig : public ABetterBaseCombine::ABetterBlockCombineConfig {
        public:  
        BlockCombineConfig(){
            //TODO: register fields and key fields here
            static const char* filterFields[] = {"FirstDate", "LastDate"};
            ABetterBaseCombine::ABetterBlockCombineConfig::registerFilterFields(filterFields, 2);
            
            static const char* noChangeUpdateFields[]={"LastDate"};
            ABetterBaseCombine::ABetterBlockCombineConfig::registerNoChangeUpdateFields(noChangeUpdateFields, 1);
            
            static const char* combineFields[] = {"OrganizationCode", "OrganizationName", "OrganizationAddress1", "OrganizationAddress2", "OrganizationCity", "OrganizationProvince", "OrganizationPostalCode", "OrganizationISOCountryCode", "OrganizationPhoneNumber"};
            ABetterBaseCombine::ABetterBlockCombineConfig::registerCombineFields(combineFields, 9);

            static const char* noHistoryMergeFields[] = {};
            ABetterBaseCombine::ABetterBlockCombineConfig::registerNoHistoryMergeFields(noHistoryMergeFields, 0);

            static const char* dateCheckFields[] = {"LastDate"};
            ABetterBaseCombine::ABetterBlockCombineConfig::registerDateCheckFields(dateCheckFields, 1);

            //History Block
            ABetterBaseCombine::ABetterBlockCombineConfig::registerHistoryBlock("Block1H", true);
            static const char* historyFields[]={"FirstDate", "LastDate", "OrganizationCode", "OrganizationName", "OrganizationAddress1", "OrganizationAddress2", "OrganizationCity", "OrganizationProvince", "OrganizationPostalCode", "OrganizationISOCountryCode", "OrganizationPhoneNumber"};
            ABetterBaseCombine::ABetterBlockCombineConfig::registerHistoryFields(historyFields, 11);
        }
    };
    static BlockCombineConfig blockConfig_;
};


#endif /* Block1BLOCK_H */
