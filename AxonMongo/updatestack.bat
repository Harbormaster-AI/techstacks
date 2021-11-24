del c:\harbormaster\techstacks\AxonMongo\AxonMongo.zip 
7z a -tzip c:\harbormaster\techstacks\AxonMongo\AxonMongo.zip c:\harbormaster\techstacks\AxonMongo\*

@call harbormaster stack_demote AxonMongo -q true
@call harbormaster stack_delete AxonMongo -q true
@call harbormaster stack_publish ./stack.publish.yml public