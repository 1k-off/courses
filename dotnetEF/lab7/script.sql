SELECT E.[BusinessEntityID],
    [FirstName] = (SELECT [FirstName] FROM [Person].[Person] P WHERE P.[BusinessEntityID] = E.[BusinessEntityID]),
    [LastName] = (SELECT [LastName] FROM [Person].[Person] P WHERE P.[BusinessEntityID] = E.[BusinessEntityID]),
    [EmailAddress],
    [PhoneNumber] = (SELECT [PhoneNumber] FROM [Person].[PersonPhone] PN WHERE PN.[BusinessEntityID] = E.[BusinessEntityID])
FROM [Person].[EmailAddress] E
ORDER BY [BusinessEntityID]

SELECT E.[BusinessEntityID], [FirstName], [LastName], [EmailAddress], [PhoneNumber]
FROM [Person].[EmailAddress] E
    JOIN [Person].[Person] P ON P.[BusinessEntityID] = E.[BusinessEntityID]
    JOIN [Person].[PersonPhone] PN ON PN.[BusinessEntityID] = E.[BusinessEntityID]
GROUP BY E.[BusinessEntityID], [FirstName], [LastName], [EmailAddress], [PhoneNumber]
ORDER BY E.[BusinessEntityID]

CREATE INDEX IDX_FirstName ON [Person].[Person] ([FirstName]);
CREATE INDEX IDX_LastName ON [Person].[Person] ([LastName]);
CREATE INDEX IDX_EmailAddress ON [Person].[EmailAddress] ([EmailAddress]);
CREATE INDEX IDX_PhoneNumber ON [Person].[PersonPhone] ([PhoneNumber]);