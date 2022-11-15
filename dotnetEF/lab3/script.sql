USE lab3
GO

IF OBJECT_ID('IISData') is not null DROP TABLE [IISData];
CREATE TABLE [IISData]
(
	Id INT IDENTITY PRIMARY KEY,
	Data XML,
	LoadedDateTime DATETIME
)

-- NEED TO COPY FILE TO THE DOCKER CONTAINER

INSERT INTO [dbo].[IISData](Data, LoadedDateTime)
SELECT CONVERT(XML, BulkColumn) AS BulkColumn, GETDATE() 
FROM OPENROWSET(BULK '/tmp/sites.xml', SINGLE_BLOB) AS x

SELECT [Data].query('/appcmd/SITE/site[@name = "gs2trello.ukad.dev"]') AS 'gs2trello.ukad.dev' FROM [dbo].[IISData] 

DECLARE @iisdata XML = (SELECT d.Data FROM [dbo].[IISData] d);

SELECT d.value('@bindingInformation', 'VARCHAR(MAX)') bindongInformation FROM @iisdata.nodes('/appcmd/SITE/site/bindings/binding') TempXML(d) 

DECLARE @domain NVARCHAR(MAX) = '1node.xyz'
DECLARE @httpBinding NVARCHAR(MAX) = '*:80:1node.xyz'
DECLARE @httpsBinding NVARCHAR(MAX) = '*:443:1node.xyz'
DECLARE @sslFlags INT = 1
UPDATE [dbo].[IISData] SET [Data].modify ('replace value of (/appcmd/SITE/site/@name)[1] with sql:variable("@domain")')
UPDATE [dbo].[IISData] SET [Data].modify ('replace value of (/appcmd/SITE/site/bindings/binding/@bindingInformation)[1] with sql:variable("@httpBinding")')
UPDATE [dbo].[IISData] SET [Data].modify ('replace value of (/appcmd/SITE/site/bindings/binding/@bindingInformation)[2] with sql:variable("@httpsBinding")')

SELECT CAST('<t>' + REPLACE(d.value('@bindingInformation', 'VARCHAR(MAX)'), ':','</t><t>') + '</t>' AS XML).value('/t[3]','NVARCHAR(MAX)') domain, d.value('@sslFlags', 'VARCHAR(MAX)') https
FROM @iisdata.nodes('/appcmd/SITE/site/bindings/binding') TempXML(d);

-- convert xml to relational
WITH Sites AS
(
     SELECT d.value('@id','INT') AS [SiteId],
            d.value('@name','NVARCHAR(MAX)') AS [Domain],
            [Data].d.query('.') AS [Node]
     FROM @iisdata.nodes('/appcmd/SITE/site') AS [Data](d)
)
SELECT s.*
      ,a.value('@physicalPath','NVARCHAR(MAX)') AS [Path]
 FROM Sites AS s
 CROSS APPLY s.[Node].nodes('site/application/virtualDirectory') As [Data](a)

 ---- convert relational data to xml
USE lab1
GO

SELECT * FROM [dbo].[User] FOR XML PATH('User'), ELEMENTS, ROOT('Users');