--Drop sequence
DROP SEQUENCE CREDIT_SEQUENCE;


-- Create sequence 
create sequence CREDIT_SEQUENCE
minvalue 1
maxvalue 999999999999999999999
start with 1
increment by 1
cache 20;