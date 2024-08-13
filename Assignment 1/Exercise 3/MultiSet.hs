module MultiSet (
    MSet(..),
    empty,
    add,
    occs,
    elems,
    subeq,
    union,
    mapMSet
) where

-- Define the MSet type constructor
data MSet a = MS [(a, Int)]
    deriving (Show)

-- Constructors

-- empty :: MSet a
-- Returns an empty MSet
empty :: MSet a
empty = MS []

-- Class Constructor Instances

-- Eq instance for MSet type: two multisets are equal if they contain the same elements with the same multiplicity, regardless of the order
instance Eq a => Eq (MSet a) where
    -- The two multisets are equal if their sorted version are equal
    MS mset1 == MS mset2 = sortedMSet mset1 == sortedMSet mset2
        where
            sortedMSet = sortMSet

-- Util functions

-- Sorting function (insertion sort) that sorts based on the first element of the tuple
sortMSet :: Ord a => [(a, Int)] -> [(a, Int)]
sortMSet [] = []
sortMSet (x:xs) = insert x (sortMSet xs)
  where
    insert y [] = [y]
    insert y (z:zs)
        | fst y <= fst z = y : z : zs
        | otherwise      = z : insert y zs