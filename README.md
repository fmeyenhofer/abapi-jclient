# abapi-jclient
Java client for the Allen Bain Atlas [RESTful API][api-doc]. It allows to query the date of the Allen Brain Atlas. 
So far the functionality is concentrated on the mouse brain (reference volumes, onthologies, section images, and SVG drawings of the anatomical regions) 

Important classes:
*  _AllenBrainAtlasRESTfulClient_ class exposes the functionality
* _AllenCache_ caches all the information on the local machine (queries, metadata, and pixel data)
* _AllenAPI_ contains RMA query catalogue

Some usage examples can be found among the sources in the __test__ directory.


[api-doc]: http://help.brain-map.org/display/api/Allen+Brain+Atlas+API