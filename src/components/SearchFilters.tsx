import { useState } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Slider } from "@/components/ui/slider";
import { useLanguage } from "@/lib/i18n/LanguageContext";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { Card, CardContent } from "@/components/ui/card";
import { Filter, X } from "lucide-react";

export function SearchFilters() {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const [isFiltersVisible, setIsFiltersVisible] = useState(false);
  const { t } = useLanguage();

  // Initialize filter state from URL params
  const [filters, setFilters] = useState({
    location: searchParams.get("location") || "",
    minPrice: Number(searchParams.get("minPrice")) || 0,
    maxPrice: Number(searchParams.get("maxPrice")) || 1000,
    bedrooms: searchParams.get("bedrooms") || "",
    bathrooms: searchParams.get("bathrooms") || "",
    guests: searchParams.get("guests") || "",
  });

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFilters((prev) => ({ ...prev, [name]: value }));
  };

  const handleSelectChange = (name: string, value: string) => {
    setFilters((prev) => ({ ...prev, [name]: value }));
  };

  const handlePriceChange = (value: number[]) => {
    setFilters((prev) => ({
      ...prev,
      minPrice: value[0],
      maxPrice: value[1],
    }));
  };

  const applyFilters = () => {
    const params = new URLSearchParams();
    
    if (filters.location) params.set("location", filters.location);
    if (filters.minPrice > 0) params.set("minPrice", filters.minPrice.toString());
    if (filters.maxPrice < 1000) params.set("maxPrice", filters.maxPrice.toString());
    if (filters.bedrooms) params.set("bedrooms", filters.bedrooms);
    if (filters.bathrooms) params.set("bathrooms", filters.bathrooms);
    if (filters.guests) params.set("guests", filters.guests);
    
    navigate(`/properties?${params.toString()}`);
    setIsFiltersVisible(false);
  };

  const resetFilters = () => {
    setFilters({
      location: "",
      minPrice: 0,
      maxPrice: 1000,
      bedrooms: "",
      bathrooms: "",
      guests: "",
    });
    navigate("/properties");
    setIsFiltersVisible(false);
  };

  const toggleFilters = () => {
    setIsFiltersVisible(!isFiltersVisible);
  };

  return (
    <div className="mb-6">
      <div className="flex justify-between items-center mb-4">
        <h2 className="text-2xl font-bold">{t('filters.findYourPerfectStay')}</h2>
        <Button 
          variant="outline" 
          size="sm" 
          onClick={toggleFilters}
          className="flex items-center gap-1"
        >
          <Filter className="h-4 w-4" />
          Filters
        </Button>
      </div>

      {isFiltersVisible && (
        <Card className="mb-4">
          <CardContent className="pt-6">
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
              <div className="space-y-2">
                <Label htmlFor="location">{t('filters.location')}</Label>
                <Input
                  id="location"
                  name="location"
                  placeholder={t('filters.any')}
                  value={filters.location}
                  onChange={handleInputChange}
                />
              </div>

              <div className="space-y-2">
                <Label>{t('filters.priceRange')}</Label>
                <div className="pt-4">
                  <Slider
                    defaultValue={[filters.minPrice, filters.maxPrice]}
                    max={1000}
                    step={10}
                    onValueChange={handlePriceChange}
                  />
                  <div className="flex justify-between mt-2 text-sm">
                    <span>${filters.minPrice}</span>
                    <span>${filters.maxPrice}+</span>
                  </div>
                </div>
              </div>

              <div className="space-y-2">
                <Label htmlFor="bedrooms">{t('filters.bedrooms')}</Label>
                <Select
                  value={filters.bedrooms}
                  onValueChange={(value) => handleSelectChange("bedrooms", value)}
                >
                  <SelectTrigger id="bedrooms">
                    <SelectValue placeholder={t('filters.any')} />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="">{t('filters.any')}</SelectItem>
                    <SelectItem value="1">1+</SelectItem>
                    <SelectItem value="2">2+</SelectItem>
                    <SelectItem value="3">3+</SelectItem>
                    <SelectItem value="4">4+</SelectItem>
                    <SelectItem value="5">5+</SelectItem>
                  </SelectContent>
                </Select>
              </div>

              <div className="space-y-2">
                <Label htmlFor="bathrooms">{t('filters.bathrooms')}</Label>
                <Select
                  value={filters.bathrooms}
                  onValueChange={(value) => handleSelectChange("bathrooms", value)}
                >
                  <SelectTrigger id="bathrooms">
                    <SelectValue placeholder={t('filters.any')} />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="">{t('filters.any')}</SelectItem>
                    <SelectItem value="1">1+</SelectItem>
                    <SelectItem value="2">2+</SelectItem>
                    <SelectItem value="3">3+</SelectItem>
                    <SelectItem value="4">4+</SelectItem>
                  </SelectContent>
                </Select>
              </div>

              <div className="space-y-2">
                <Label htmlFor="guests">{t('filters.guests')}</Label>
                <Select
                  value={filters.guests}
                  onValueChange={(value) => handleSelectChange("guests", value)}
                >
                  <SelectTrigger id="guests">
                    <SelectValue placeholder={t('filters.any')} />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="">{t('filters.any')}</SelectItem>
                    <SelectItem value="1">1+</SelectItem>
                    <SelectItem value="2">2+</SelectItem>
                    <SelectItem value="4">4+</SelectItem>
                    <SelectItem value="6">6+</SelectItem>
                    <SelectItem value="8">8+</SelectItem>
                  </SelectContent>
                </Select>
              </div>

              <div className="flex items-end space-x-2 md:col-span-1">
                <Button onClick={applyFilters} className="flex-1">{t('filters.applyFilters')}</Button>
                <Button variant="outline" onClick={resetFilters} className="flex items-center gap-1">
                  <X className="h-4 w-4" />
                  {t('filters.reset')}
                </Button>
              </div>
            </div>
          </CardContent>
        </Card>
      )}
    </div>
  );
}