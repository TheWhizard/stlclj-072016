#ifndef Block2BLOCK_H
#define Block2BLOCK_H

using namespace ExchangeImplementationUtility;

class Block2Block  : public ABetterChildCombine
{
public:
    Block2Block(commonServices *parmpcs, CXBlock* parentBlock):
        ABetterChildCombine(parmpcs, parentBlock, "Block2")
    {
        init();
    }

    Block2Block(pxDataManager *parmnewpdm, pxDataManager *parmcmppdm, TransactionControlServices *parmptcs, commonServices *parmpcs, CXBlock* parentBlock):
        ABetterChildCombine(parmnewpdm, parmcmppdm, parmptcs, parmpcs, parentBlock, "Block2")
    {
        init();
    }

    ~Block2Block(){};

    void init() {
        setCombineMetadata(&Block2Block::blockConfig_);
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
            
            static const char* combineFields[] = {"SubjectID", "SubjectFirstName", "SubjectMiddleName", "SubjectLastName", "SubjectDateOfBirth", "SubjectAddress2", "SubjectCity", "SubjectProvince", "SubjectISOCountryCode", "SubjectPersonalEmailAddress", "SubjectHomePhoneNumber", "SubjectMobilePhoneNumber"};
            ABetterBaseCombine::ABetterBlockCombineConfig::registerCombineFields(combineFields, 12);

            static const char* noHistoryMergeFields[] = {};
            ABetterBaseCombine::ABetterBlockCombineConfig::registerNoHistoryMergeFields(noHistoryMergeFields, 0);

            static const char* dateCheckFields[] = {"LastDate"};
            ABetterBaseCombine::ABetterBlockCombineConfig::registerDateCheckFields(dateCheckFields, 1);

            //History Block
            ABetterBaseCombine::ABetterBlockCombineConfig::registerHistoryBlock("Block2H", true);
            static const char* historyFields[]={"FirstDate", "LastDate", "SubjectID", "SubjectFirstName", "SubjectMiddleName", "SubjectLastName", "SubjectDateOfBirth", "SubjectAddress1", "SubjectAddress2", "SubjectCity", "SubjectProvince", "SubjectPostalCode", "SubjectISOCountryCode", "SubjectPersonalEmailAddress", "SubjectHomePhoneNumber", "SubjectMobilePhoneNumber"};
            ABetterBaseCombine::ABetterBlockCombineConfig::registerHistoryFields(historyFields, 16);
        }
    };
    static BlockCombineConfig blockConfig_;
};


#endif /* Block2BLOCK_H */
