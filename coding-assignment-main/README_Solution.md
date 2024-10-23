# Coding Assignment API 

/transformToXML/v3 - API is an interface to transform list of transactions into one XML File per date 

# Approach 

Processing a list of transactions with optimized memory usage: Usina a Multi-thread API, where the Processor process the  
list of transactions received and send each day's transaction to Writer to continuously write into XML, so we dont 
load all data into memory and prevent from OutOfMemory. The communication between the Processor and 
the Writer is via BlockQueue.

# Design 

CodingAssignmentApplication - Bootstrapping the spring application
TransformToXML - API (/transformToXML/v3) exposed to process list of transactions into XML
Processor - Class that process transactions for each date and pass the data to the Writer. Processor is the producer that 
produce the data to the Consumer to write into XML 
Writer - The consumer class that receive data from the Processor through Queue and it writes the data received into XML file
WriteXML - Class that generates and store xml in the OUTPUT folder 
SwaggerConfig - Generates API Documentation and provide an interactive UI to explore and test the RESTful APIs 

# Usage of API 

http://localhost:8080/swagger-ui/index.html#/ - With the usage of Swagger index, below APIs can be triggered 
/transformToXML  - To write the transactions into XML in OUTPUT folder
/deleteFolder - Utility to Delete the created OUTPUT folder before any new request 

# Assumptions 

All the transactions in the list are of same currency and all the fields of Transaction DTO is passed 
transactionType field of each Transaction needs to be either CREDIT/DEBIT - case-sensitive
