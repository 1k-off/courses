USE [lab4]
GO

/****** Object:  Table [dbo].[User]    Script Date: 30.11.2022 12:13:18 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO
IF OBJECT_ID('User') is not null DROP TABLE [User];
CREATE TABLE [dbo].[User](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[FirstName] [nvarchar](max) NULL,
	[LastName] [nvarchar](max) NULL,
	[Login] [nvarchar](max) NOT NULL,
	[Discriminator] [nvarchar](max) NOT NULL,
	[Role] [nvarchar](max) NULL,
	[VpnClient_Role] [nvarchar](max) NULL,
 CONSTRAINT [PK_User] PRIMARY KEY CLUSTERED 
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO

INSERT INTO [dbo].[User]  ([FirstName], [LastName], [Login], [Discriminator], [Role], [VpnClient_Role])
    VALUES ('Bogdan', 'Kosarevskyi', 'kosar', 'Admin', 'Global Administrator', NULL),
		   ('Unknown', 'Fox', 'fox', 'Admin', 'System Administrator', NULL),
           ('John', 'Doe', 'jd', 'User', NULL, NULL),
           ('Marty', 'McFly', 'marty', 'User', NULL, NULL),
		   ('Emmeth', 'Brown', 'doc', 'User', NULL, NULL);

DECLARE @cnt INT = 0;
WHILE @cnt < 50
BEGIN
   INSERT INTO [dbo].[User]
           ([FirstName]
           ,[LastName]
           ,[Login]
           ,[Discriminator]
           ,[Role]
           ,[VpnClient_Role])
     VALUES
           ('firstname' + CAST(@cnt as nvarchar(max)),
           'lastname' + CAST(@cnt as nvarchar(max)),
           'client' + CAST(@cnt as nvarchar(max)),
           'VpnClient',
           NULL,
           NULL);
   SET @cnt = @cnt + 1;
END;
UPDATE [dbo].[User]
   SET [Role] = 'INTERNAL'
 WHERE  Id > 5 AND ( Id % 2 ) = 0 
GO
