USE Lab2
GO

IF OBJECT_ID('TreeSQL') is not null DROP TABLE TreeSQL;
CREATE TABLE TreeSQL(
	TreeID hierarchyid PRIMARY KEY,
	NodeName NVARCHAR(30) UNIQUE
);
GO

SELECT 'INSERT A NODE BELOW GIVEN' AS [Task 1];

-- Insert root node (level 0)
INSERT TreeSQL(TreeID, NodeName) VALUES (hierarchyid::GetRoot(), 'A');

-- Get root
DECLARE @Root hierarchyid;
SELECT @Root = hierarchyid::GetRoot() FROM TreeSQL;

-- Insert A right child first (level 1)
-- AB
INSERT TreeSQL(TreeID, NodeName) VALUES (@Root.GetDescendant(NULL, NULL), 'AB');
DECLARE @AB hierarchyid = (SELECT TreeID FROM TreeSQL WHERE NodeName = 'AB'); 

-- Insert A left child before right (level 1)
-- AA
INSERT TreeSQL(TreeID, NodeName) VALUES (@Root.GetDescendant(NULL, @AB), 'AA');
DECLARE @AA hierarchyid = (SELECT TreeID FROM TreeSQL WHERE NodeName = 'AA'); 

-- Insert AA childs
DECLARE @AA_ID_STR nvarchar(max) = (SELECT TreeID.ToString() FROM TreeSQL WHERE NodeName = 'AA');
INSERT INTO TreeSQL(TreeID, NodeName) SELECT CONCAT(@AA_ID_STR, '1/'), 'AAA';
INSERT INTO TreeSQL(TreeID, NodeName) SELECT CONCAT(@AA_ID_STR, '2/'), 'AAB'

-- Insert AB childs starting from central
INSERT TreeSQL(TreeID, NodeName) VALUES (@AB.GetDescendant(NULL, NULL), 'ABB');
DECLARE @ABB hierarchyid = (SELECT TreeID FROM TreeSQL WHERE NodeName = 'ABB'); 
INSERT TreeSQL(TreeID, NodeName) VALUES (@AB.GetDescendant(NULL, @ABB), 'ABA');
INSERT TreeSQL(TreeID, NodeName) VALUES (@AB.GetDescendant(@ABB, NULL), 'ABC');

-- Insert ABB Childrens
DECLARE @ABB_ID_STR nvarchar(max) = (SELECT TreeID.ToString() FROM TreeSQL WHERE NodeName = 'ABB');
INSERT INTO TreeSQL(TreeID, NodeName) SELECT CONCAT(@ABB_ID_STR, '1/'), 'ABBA';
INSERT INTO TreeSQL(TreeID, NodeName) SELECT CONCAT(@ABB_ID_STR, '2/'), 'ABBB'

-- Select all
SELECT 
	TreeID AS [TreeID], 
	TreeID.ToString() AS [Path], 
	TreeID.GetLevel() AS [Level], 
	NodeName 
FROM TreeSQL ORDER BY TreeID;

SELECT 'GET A BRANCH OF TREE STARTING FROM THE GIVEN NODE' AS [Task 2];
-- Select full branch
DECLARE @nodeId hierarchyid = (SELECT TreeID FROM TreeSQL WHERE NodeName = 'AAA');
SELECT TreeID, TreeID.ToString() AS [Path], NodeName FROM TreeSQL WHERE TreeID.IsDescendantOf(@nodeId) = 1;
---- DECLARE @IN_ID nvarchar(max) = (SELECT TreeID.ToString() FROM TreeSQL WHERE NodeName = 'ABB');
---- SELECT TreeID, TreeID.ToString() AS [Path], NodeName FROM TreeSQL WHERE TreeID.ToString() LIKE @IN_ID + '%' ORDER BY TreeID;
-- or full branch to root
---- SELECT TreeID, TreeID.ToString() AS [Path], NodeName FROM TreeSQL WHERE TreeID.ToString() LIKE LEFT(@IN_ID, 2) + '%'OR TreeID=@Root  ORDER BY TreeID;

SELECT 'SELECT ALL PARENTS OF A GIVEN NODE' AS [Task 3];
-- Select all parents
WITH Ancestors(Id, AncestorId, NodeName) AS
(
      SELECT 
		TreeID, 
		TreeID.GetAncestor(1), 
		NodeName 
	  FROM TreeSQL WHERE NodeName = 'ABBA'
      UNION ALL
      SELECT 
		t.TreeID, 
		t.TreeID.GetAncestor(1), 
		t.NodeName 
	  FROM TreeSQL t INNER JOIN Ancestors a ON t.TreeID = a.AncestorId
)
SELECT *, Id.ToString() AS [Path] FROM Ancestors;
-- Alternate way
--DECLARE @node hierarchyid

--SELECT @node = TreeID FROM TreeSQL WHERE NodeName = 'ABBA';

--SELECT
--    TreeID, TreeID.ToString() AS [Path], 
--    TreeID.GetLevel() AS [Level],
--    TreeID.GetAncestor(1),
--    NodeName
--FROM 
--    TreeSQL
--WHERE 
--    @node.IsDescendantOf(TreeID) = 1


SELECT 'DELETE A TREE BRANCH' AS [Task 4];
-- Delete a branch
DECLARE @dNodeId hierarchyid = (SELECT TreeID FROM TreeSQL WHERE NodeName = 'AA');
DELETE FROM TreeSQL WHERE TreeID IN (
	SELECT d.TreeID FROM TreeSQL d WHERE TreeID.IsDescendantOf(@dNodeId) = 1
);
-- Select all
SELECT 
	TreeID AS [TreeID], 
	TreeID.ToString() AS [Path], 
	TreeID.GetLevel() AS [Level], 
	NodeName 
FROM TreeSQL ORDER BY TreeID;
