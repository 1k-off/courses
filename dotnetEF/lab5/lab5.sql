-- FILE 1
SET TRANSACTION ISOLATION LEVEL SERIALIZABLE
BEGIN TRANSACTION
UPDATE [dbo].[User] SET [Role] = NULL WHERE Id = 1;
UPDATE [dbo].[User] SET [Role] = 'Global Administrator' WHERE Id = 3;
WAITFOR DELAY '00:00:15';
DECLARE @user int = (SELECT Id FROM [dbo].[USER] WHERE Id = 3); 
IF @user is null ROLLBACK TRANSACTION
ELSE COMMIT TRANSACTION

-- FILE 2
SET LOCK_TIMEOUT 8000;
BEGIN TRANSACTION
UPDATE [dbo].[User] SET [FirstName] = 'Johnny' WHERE Id = 3;
COMMIT

-- FILE 3
SELECT
request_session_id AS spid,
resource_type AS restype,
DB_NAME(resource_database_id) AS dbname,
request_mode AS mode,
request_status AS status
FROM sys.dm_tran_locks  WHERE request_status = 'WAIT';

-- FILE 4
/****** Script for SelectTopNRows command from SSMS  ******/
SELECT TOP (1000) [Id]
      ,[FirstName]
      ,[LastName]
      ,[Login]
      ,[Discriminator]
      ,[Role]
      ,[VpnClient_Role]
  FROM [lab1].[dbo].[User]
