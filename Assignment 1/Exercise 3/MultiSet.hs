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

---------------
-- Constructors
---------------

-- empty :: MSet a
-- Returns an empty MSet
empty :: MSet a
empty = MS []

------------------------------
-- Class Constructor Instances
------------------------------

-- Eq instance for MSet using the subset method
instance Eq a => Eq (MSet a) where
    -- The two multisets are equal if both are "strict" subset of each other 
    -- (meaning that the multiplicity of the elements must be the same)
    MS mset1 == MS mset2 = strictSubeq (MS mset1) (MS mset2) && strictSubeq (MS mset2) (MS mset1)

-- Foldable instance for MSet (using foldr to create the minimal implementation for Foldable)
instance Foldable MSet where
    foldr f acc (MS []) = acc
    foldr f acc (MS ((x, n):xs)) = foldr f (foldr f acc (MS xs)) (replicate n x)

{-
Explanation for not implementing Functor:

The MSet type can't be made an instance of Functor because it would break the Functor laws. 
Specifically, fmap is supposed to apply a function to each element independently, without altering the structure of the functor. 
However, in an MSet, if fmap (see the mapMSet implementation in Util functions section) mapped two distinct elements to the same result, 
we would need to combine their multiplicities to keep the MSet well-formed. 
This changes the structure, which isn't allowed by Functor laws. 
The structure should stay the same, and only the values should be modified by fmap 
(“The structure of the functor remains unchanged and only the values are modified.” - https://wiki.haskell.org/Functor#Functor_Laws). 
This means that MSet doesn’t fit the requirements for Functor.
-}

-------------
-- Operations
-------------

-- add an element to the MSet, increasing its multiplicity if it already exists
add :: Eq a => MSet a -> a -> MSet a
add (MS mset) v = MS (addHelper mset v)
  where
    -- function to add element to a multiset
    addHelper :: Eq a => [(a, Int)] -> a -> [(a, Int)]
    addHelper [] v = [(v, 1)]
    addHelper ((x, n):xs) v
        | x == v    = (x, n+1) : xs
        | otherwise = (x, n) : addHelper xs v

-- return the number of occurrences of an element in the MSet
occs :: Eq a => MSet a -> a -> Int
occs (MS mset) v = case lookup v mset of
    Just n  -> n
    Nothing -> 0

-- return a list containing all the distinct elements of the MSet
elems :: MSet a -> [a]
elems (MS mset) = map fst mset

-- check if the first MSet is a subset of the second MSet (ToDo: fix this implementation)
-- subeq :: Eq a => MSet a -> MSet a -> Bool
-- subeq (MS mset1) mset2 = all checkElem mset1
--  where
    -- function to check if an element in mset1 has at least the same multiplicity in mset2
    -- checkElem :: (a, Int) -> Bool
    -- checkElem (x, n) = occs mset2 x >= n

-- a more straightforward version of subeq
subeq :: Eq a => MSet a -> MSet a -> Bool
subeq (MS []) _ = True
subeq (MS ((x, n):xs)) mset2
  | occs mset2 x >= n = subeq (MS xs) mset2
  | otherwise         = False

-- return a new MSet with the union of the two input MSets
union :: Eq a => MSet a -> MSet a -> MSet a
union (MS mset1) (MS mset2) = MS (unionMSet mset1 mset2)
  where
    -- function to combine elements from the second MSet into the first MSet
    unionMSet :: Eq a => [(a, Int)] -> [(a, Int)] -> [(a, Int)]
    unionMSet mset1 [] = mset1
    unionMSet mset1 ((x, n):xs) =
        unionMSet (addOrUpdate x n mset1) xs

    -- function to add or update an element in the MSet
    addOrUpdate :: Eq a => a -> Int -> [(a, Int)] -> [(a, Int)]
    addOrUpdate x n [] = [(x, n)]
    addOrUpdate x n ((y, m):ys)
        | x == y    = (y, m + n) : ys
        | otherwise = (y, m) : addOrUpdate x n ys

-----------------
-- Util functions
-----------------

-- Applies a function to all elements of the MSet.
mapMSet :: (a -> b) -> MSet a -> MSet b
mapMSet f (MS mset) = MS (map (applyFunc f) mset)
  where
    -- function to apply func to 1st elem of MSet type
    applyFunc :: (a -> b) -> (a, Int) -> (b, Int)
    applyFunc func (x, n) = (func x, n)

-- Check if one MSet is a subset with the same multiplicity as another MSet (ToDo: fix this implementation)
-- strictSubeq :: Eq a => MSet a -> MSet a -> Bool
-- strictSubeq (MS mset1) (MS mset2) = all checkElem mset1
  --where
    -- function to check if an element in mset1 has the same multiplicity in mset2
    -- checkElem :: (a, Int) -> Bool
    -- checkElem (x, n) = occs (MS mset2) x == n

  -- A more straightforward and isolated version of strictSubeq 
strictSubeq :: Eq a => MSet a -> MSet a -> Bool
strictSubeq (MS []) _ = True
strictSubeq (MS ((x, n):xs)) mset2
  | occs mset2 x == n = strictSubeq (MS xs) mset2
  | otherwise         = False
