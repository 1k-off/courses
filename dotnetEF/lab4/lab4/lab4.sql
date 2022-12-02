USE [lab4]
GO

DROP PROCEDURE [dbo].[sp_UpdateVpnRoleForMigratedClients]
GO
CREATE PROCEDURE [dbo].[sp_UpdateVpnRoleForMigratedClients] @newRole nvarchar(max), @recordsUpdated INT OUTPUT, @errorCount INT OUTPUT
AS

DECLARE
	@Id int,
	@Login nvarchar(max),
	@Role nvarchar(max),
	@VpnClient_Role nvarchar(max)

DECLARE @UpdatedData TABLE ([Login] NVARCHAR(max),  [VpnClient_Role] NVARCHAR(max))

SET @recordsUpdated = 0;
SET @errorCount = 0;

DECLARE cur CURSOR FOR
  SELECT [Id], [Login], [Role], [VpnClient_Role] FROM [dbo].[User] ORDER BY [Login]
OPEN cur
FETCH NEXT FROM cur INTO @Id, @Login, @Role, @VpnClient_Role

While @@Fetch_Status = 0 BEGIN
	--BEGIN TRY
		UPDATE [dbo].[User]
		SET [VpnClient_Role] = CASE 
			WHEN @Role IS NULL THEN @newRole
			WHEN @Role = 'INTERNAL' THEN 'I_UA'
			ELSE @VpnClient_Role
		END
		WHERE [VpnClient_Role] IS NULL AND [Id] = @Id;
		IF @Login = 'jd' BEGIN
			DECLARE @errResult int = (SELECT 1 / 0);
		END;
	--END TRY
	--BEGIN CATCH
	--	SET @errorCount += 1
	--		SELECT
	--			ERROR_NUMBER() AS ErrorNumber,
	--			ERROR_STATE() AS ErrorState,
	--			ERROR_SEVERITY() AS ErrorSeverity,
	--			ERROR_PROCEDURE() AS ErrorProcedure,
	--			ERROR_LINE() AS ErrorLine,
	--			ERROR_MESSAGE() AS ErrorMessage;
	--END CATCH
	IF @Role IS NULL BEGIN
		DECLARE @rn NVARCHAR(max) = (SELECT [VpnClient_Role] FROM [dbo].[User] WHERE [Id] = @Id)
		IF @rn = @newRole BEGIN
			SET @recordsUpdated += 1;
		END;
	END;
	IF @Role = 'INTERNAL' BEGIN
		DECLARE @ri NVARCHAR(max) = (SELECT [VpnClient_Role] FROM [dbo].[User] WHERE [Id] = @Id)
		IF @ri = 'I_UA' BEGIN
			SET @recordsUpdated += 1;
		END;
	END;
	INSERT INTO @UpdatedData ([Login], [VpnClient_Role]) SELECT [Login], [VpnClient_Role] FROM [dbo].[User] WHERE [Id] = @Id
FETCH NEXT FROM cur INTO @Id, @Login, @Role, @VpnClient_Role
END
CLOSE cur
DEALLOCATE cur
SELECT * FROM @UpdatedData
RETURN;
GO


--DECLARE	@updatedRecordsCount int
--DECLARE @errCount int

--EXEC	[dbo].[sp_UpdateVpnRoleForMigratedClients]
--		@newRole = N'C_TEST',
--		@recordsUpdated = @updatedRecordsCount OUTPUT,
--		@errorCount = @errCount OUTPUT;

--SELECT	'Records updated' = @updatedRecordsCount, 'Error count' = @errCount

--GO
