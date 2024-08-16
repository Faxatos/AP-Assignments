module TestMSet where

import MultiSet

import Data.Char (toLower)
import Data.List (sort)
import System.IO (readFile, writeFile)

-------------
-- Util functions
-------------

-- read a text file and return an MSet with the ciao of each word and its multiplicity
readMSet :: FilePath -> IO (MSet String)
readMSet fileName = do
    content <- readFile fileName
    let wordsList = words content
    let ciaoList = map ciao wordsList
    return (foldl add empty ciaoList)

-- write an MSet to a file, one element per line with its multiplicity
writeMSet :: MSet String -> FilePath -> IO ()
writeMSet (MS mset) fileName = do
    let linesToWrite = map (\(elem, count) -> elem ++ " - " ++ show count) mset
    writeFile fileName (unlines linesToWrite)

-- convert a string to its "ciao" (characters in alphabetical order) form, so sorted characters in lower case
ciao :: String -> String
ciao = sort . map toLower

-- | Main function for testing
main :: IO ()
main = do
    -- Load multisets from files
    m1 <- readMSet "../../aux_files/anagram.txt"
    m2 <- readMSet "../../aux_files/anagram-s1.txt"
    m3 <- readMSet "../../aux_files/anagram-s2.txt"
    m4 <- readMSet "../../aux_files/margana2.txt"

    -- debug prints
    -- writeMSet m1 "./output/m1.txt"
    -- writeMSet m2 "./output/m2.txt"
    -- writeMSet m3 "./output/m3.txt"
    -- writeMSet m4 "./output/m4.txt"

    -- Check and print whether m1 and m4 are not equal but have the same elements
    if not (m1 == m4) && elems m1 == elems m4
        then putStrLn "i. Multisets m1 and m4 are not equal, but they have the same elements"
        else putStrLn "i. The fact 'Multisets m1 and m4 are not equal, but they have the same elements' is not true"

    -- Check and print whether m1 is equal to the union of m2 and m3
    let m2UnionM3 = union m2 m3
    if m1 == m2UnionM3
        then putStrLn "ii. Multiset m1 is equal to the union of multisets m2 and m3"
        else putStrLn "ii. The fact 'Multiset m1 is equal to the union of multisets m2 and m3' is not true"

    -- Write m1 and m4 to output files
    writeMSet m1 "./output/anag-out.txt"
    writeMSet m4 "./output/gana-out.txt"